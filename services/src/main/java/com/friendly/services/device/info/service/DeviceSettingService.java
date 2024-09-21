package com.friendly.services.device.info.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.*;
import com.friendly.commons.models.device.response.DeviceConfigsResponse;
import com.friendly.commons.models.device.response.DeviceObjectsResponse;
import com.friendly.commons.models.device.response.DeviceSimplifiedParamsResponse;
import com.friendly.commons.models.device.response.ItemTaskIdsResponse;
import com.friendly.commons.models.device.setting.*;
import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.activity.service.TaskService;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterEntity;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.ftacs.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.DeviceActivityType.*;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FIELD_CAN_NOT_BE_EMPTY;
import static com.friendly.services.device.parameterstree.utils.ParameterUtil.getShortName;

/**
 * Service that exposes the base functionality for interacting with {@link Device} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceSettingService {

    @NonNull
    private final UserService userService;

    @NonNull
    private final TaskService taskService;

    @NonNull
    private final StatisticService statisticService;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final ParameterService parameterService;

    @NonNull
    private final ParameterNameService parameterNameService;

    public DeviceObjectsResponse getDeviceParameters(final String token, final DeviceParametersBody body) {
        jwtService.getSession(token);

        return new DeviceObjectsResponse(parameterService.getDeviceParameters(body.getDeviceId(), body.getFullName()));
    }

    public ItemTaskIdsResponse getCurrentDeviceParams(final String token, final CurrentDeviceParametersBody body) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final Long deviceId = body.getDeviceId();

        final String userName = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        deviceIds.getId().add(deviceId.intValue());
        final ParameterDataListWS parameterListWS = new ParameterDataListWS();
        final StringArrayWS paramNamesWS = new StringArrayWS();
        paramNamesWS.getString().addAll(body.getFullNames());
        parameterListWS.setParameterNameList(paramNamesWS);
        final StringArrayWS paramsWS = new StringArrayWS();
        paramsWS.getString().addAll(Arrays.asList("names", "values", "attributes"));
        parameterListWS.setDataList(paramsWS);

        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .getParameterDataListFromCPE(deviceIds, parameterListWS, 3, true, userName, null);

            return new ItemTaskIdsResponse(taskService.getTaskIds(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    @Transactional
    public ItemTaskIdsResponse updateDeviceParams(final String token, UpdateDeviceParamsBody body) {
        final Session session = jwtService.getSession(token);
        List<Long> ids = updateDeviceParams(body.getDeviceId(), body.getRequest(), session, false, token);

        return new ItemTaskIdsResponse(ids);
    }

    @Transactional
    public List<Long> updateDeviceParams(final Long deviceId,
                                         final DeviceParameterUpdateRequest request,
                                         final Session session,
                                         final boolean isGroup, String token) {
        if (request.getParameters() == null || request.getParameters().isEmpty()) {
            return Collections.emptyList();
        }
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String updater = clientType.name().toUpperCase() + "/" + user.getUsername();
        final Boolean reprovision = request.getReprovision();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        deviceIds.getId().add(deviceId.intValue());
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        final CpeParamListWS cpeParamListWS = new CpeParamListWS();

        request.getParameters()
                .forEach(p -> {
                    if (!(p.getValue() instanceof List)) {
                        final CpeParamWS cpeParamWS = new CpeParamWS();
                        if (p.getValue() instanceof Integer) {
                            cpeParamWS.setValue(String.valueOf(p.getValue()));
                        } else {
                            cpeParamWS.setValue((String) p.getValue());
                        }
                        cpeParamWS.setName(p.getFullName());
                        cpeParamWS.setReprovision(reprovision);
                        cpeParamListWS.getCPEParam().add(cpeParamWS);
                    } else {
                        List<CpeParameterEntity> entities
                                = parameterService.findAllByCpeIdAndFullNameLike(deviceId, p.getFullName() + "._");
                        List<LinkedHashMap<String, Object>> list = (List) p.getValue();
                        List<LinkedHashMap<String, Object>> listCopy = new ArrayList<>(list);
                        entities.forEach(entity -> {
                            ModifyResponse modifyResponse = mapContainsKey(entity, list, listCopy);
                            if (modifyResponse.getModifyOption() == ModifyOption.DELETE) {
                                List<String> strings = new ArrayList<>();
                                strings.add(parameterNameService.getNameById(entity.getNameId()));
                                deleteDeviceParameters(token, new DeleteParametersBody(deviceId.intValue(), new ParameterNamesRequest(strings)));
                            } else if (modifyResponse.getModifyOption() == ModifyOption.MODIFY) {
                                final CpeParamWS cpeParamWS = new CpeParamWS();
                                cpeParamWS.setValue((String) modifyResponse.getMap().get("value"));
                                cpeParamWS.setName(p.getFullName() + "."
                                        + modifyResponse.getMap().get("key"));
                                cpeParamWS.setReprovision(reprovision);
                                cpeParamListWS.getCPEParam().add(cpeParamWS);
                            }
                        });
                        listCopy.forEach(map -> sendAddObject(deviceId, request, token, p, map, reprovision));
                    }
                });


        try {
            if (cpeParamListWS.getCPEParam().isEmpty()) {
                return new ArrayList<>();
            }
            final TransactionIdResponse response = AcsProvider.getAcsWebService(clientType)
                    .setCPEParams(deviceIds, cpeParamListWS, 2, isGroup,
                            request.getPush(), false, null, updater);
            final String note = cpeParamListWS.getCPEParam()
                    .stream()
                    .map(p -> p.getName() + "=" + p.getValue())
                    .collect(Collectors.joining("; "));
            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(CHANGE_PARAMETERS)
                    .deviceId(deviceId)
                    .groupId(groupId)
                    .serial(serial)
                    .note(note)
                    .build());

            return taskService.getTaskIds(response.getTransactionId());
        } catch (Exception_Exception e) {
            log.error("Request: " + request, e);
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    private void sendAddObject(Long deviceId, DeviceParameterUpdateRequest request, String token, Parameter p, LinkedHashMap<String, Object> map, Boolean reprovision) {
        List<LinkedHashMap<String, Object>> l = new ArrayList<>();
        LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("key", map.get("key"));
        valueMap.put("value", map.get("value"));
        l.add(valueMap);
        addParamObject(token, new AddObjectBody(deviceId,
                new AddObjectRequest(request.getPush(), reprovision,
                        removeLettersAfterLastDot(p.getFullName()),
                        Collections.singletonList(
                                new NewObjectParam(getShortName(p.getFullName()), l))
                )));
    }

    public static String removeLettersAfterLastDot(String str) {
        int lastIndex = str.lastIndexOf(".");
        if (lastIndex != -1) {
            return str.substring(0, lastIndex + 1);
        } else {
            return str;
        }
    }

    private ModifyResponse mapContainsKey(final CpeParameterEntity entity,
                                          final List<LinkedHashMap<String, Object>> list,
                                          final List<LinkedHashMap<String, Object>> listCopy) {
        for (LinkedHashMap<String, Object> map : list) {
            String fullName = parameterNameService.getNameById(entity.getNameId());
            char lastChar = fullName.charAt(fullName.length() - 1);
            Integer number = Character.getNumericValue(lastChar);
            if (map.get("key") == number) {
                listCopy.remove(map);
                if (map.get("value").equals(entity.getValue())) {
                    return new ModifyResponse(ModifyOption.EQUALS, map);
                } else {
                    return new ModifyResponse(ModifyOption.MODIFY, map);
                }
            }
        }
        return new ModifyResponse(ModifyOption.DELETE, null);
    }


    public ItemTaskIdsResponse addParamObject(final String token, final AddObjectBody body) {
        final Session session = jwtService.getSession(token);

        return new ItemTaskIdsResponse(addObject(body.getDeviceId(), body.getRequest(), session));
    }

    public List<Long> addObject(final Long deviceId, final AddObjectRequest request, final Session session) {
        if (StringUtils.isBlank(request.getObjectName())) {
            throw new FriendlyIllegalArgumentException(FIELD_CAN_NOT_BE_EMPTY, "objectName");
        }
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final Boolean reprovision = request.getReprovision();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        deviceIds.getId().add(deviceId.intValue());
        final CpeObjectListWS cpeObjectListWS = new CpeObjectListWS();
        final CpeObjectWS cpeObjectWS = new CpeObjectWS();
        final String objectName = request.getObjectName();
        cpeObjectWS.setObjectName(objectName);
        cpeObjectWS.setReprovision(reprovision);
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        final List<NewObjectParam> parameters = request.getParameters();
        if (parameters != null) {
            final CpeObjectParamListWS cpeParamListWS = new CpeObjectParamListWS();
            parameters
                    .forEach(p -> {
                        if (!(p.getValue() instanceof List)) {
                            final CpeObjectParamWS cpeParamWS = new CpeObjectParamWS();
                            if (p.getValue() instanceof Integer) {
                                cpeParamWS.setParamValue(String.valueOf(p.getValue()));
                            } else {
                                cpeParamWS.setParamValue((String) p.getValue());
                            }
                            cpeParamWS.setParamName(p.getShortName());
                            cpeParamListWS.getCPEObjectParam().add(cpeParamWS);
                        } else {
                            List<LinkedHashMap<String, Object>> list = (List) p.getValue();
                            list.forEach(l -> {
                                cpeObjectWS.setObjectName(objectName);
                                final CpeObjectParamWS cpeParamWS = new CpeObjectParamWS();
                                cpeParamWS.setParamValue((String) l.get("value"));
                                cpeParamWS.setParamName(p.getShortName() + "." + l.get("key"));
                                cpeParamListWS.getCPEObjectParam().add(cpeParamWS);
                            });
                        }

                    });

            cpeObjectWS.setCPEObjectParamList(cpeParamListWS);
        }
        cpeObjectListWS.getCpeObject().add(cpeObjectWS);

        try {
            final TransactionIdResponse response = AcsProvider.getAcsWebService(clientType)
                    .addCPEObject(deviceIds, cpeObjectListWS, false, 3,
                            request.getPush(), creator);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(ADD_OBJECT)
                    .deviceId(deviceId)
                    .groupId(groupId)
                    .note(request.getObjectName())
                    .serial(serial)
                    .build());

            return taskService.getTaskIds(response.getTransactionId());
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public void deleteParamObjects(final String token, final DeleteDeviceObjectsBody body) {

        final DeviceDeleteObjectRequest request = body.getRequest();
        final Long deviceId = body.getDeviceId();

        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String username = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        deviceIds.getId().add(deviceId.intValue());
        final ACSWebService acsWebService = AcsProvider.getAcsWebService(clientType);
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();

        request.getObjectNames()
                .forEach(name -> {
                    final StringArrayWS objectNames = new StringArrayWS();
                    objectNames.getString().add(name);

                    try {
                        acsWebService.deleteCPEObject(deviceIds, objectNames, 3,
                                request.getPush(), null, username);
                    } catch (Exception_Exception e) {
                        throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
                    }
                });

        statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                .userId(session.getUserId())
                .clientType(clientType)
                .activityType(DELETE_OBJECT)
                .deviceId(deviceId)
                .groupId(groupId)
                .serial(serial)
                .note(StringUtils.join(request.getObjectNames(), "; "))
                .build());
    }

    public DeviceSimplifiedParamsResponse getDeviceSimplifiedView(final String token, final Long deviceId) {
        Session session = jwtService.getSession(token);

        final List<DeviceSimplifiedParams> simplifiedParams = DeviceUtils.getDeviceSimplifiedParams(session.getClientType(), parameterService.getRootParamName(deviceId)).stream()
                .map(p -> new DeviceSimplifiedParams(p.getName(), p.getItems())).collect(Collectors.toList());

        if (simplifiedParams == null) {
            return new DeviceSimplifiedParamsResponse(Collections.emptyList());
        }

        List<DeviceSimplifiedParams> deviceSimplifiedParams = simplifiedParams.stream()
                .map(s -> {
                    final List<DeviceSimplifiedParam> params =
                            s.getItems()
                                    .stream()
                                    .filter(i -> parameterService.isParamExist(deviceId, i.getFullName()))
                                    .collect(Collectors.toList());
                    s.setItems(params);
                    return s;
                })
                .filter(s -> s.getItems() != null && !s.getItems().isEmpty())
                .collect(Collectors.toList());
        return new DeviceSimplifiedParamsResponse(deviceSimplifiedParams);
    }

    @Transactional
    public void deleteDeviceParameters(String token, DeleteParametersBody body) {

        final Session session = jwtService.getSession(token);
        final ParameterNamesRequest request = body.getRequest();
        final Integer deviceId = body.getDeviceId();
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String username = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        deviceIds.getId().add(deviceId);
        final ACSWebService acsWebService = AcsProvider.getAcsWebService(clientType);
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId.longValue());
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();

        final StringArrayWS params = new StringArrayWS();
        request.getParameterNames()
                .forEach(name -> params.getString().add(name));
        try {
            acsWebService.deleteCPEObject(deviceIds, params, 3,
                    false, null, username);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }

        statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                .userId(session.getUserId())
                .clientType(clientType)
                .activityType(DELETE_PARAMETER)
                .deviceId(deviceId.longValue())
                .groupId(groupId)
                .serial(serial)
                .note(StringUtils.join(request.getParameterNames(), "; "))
                .build());

    }

    public DeviceConfigsResponse getDeviceConfig(final String token, final DeviceConfigType type) {
        jwtService.getSession(token);

        return new DeviceConfigsResponse(DeviceUtils.getDeviceConfig(type));
    }

    public ItemTaskIdsResponse wifiAutoRescan(String token, WifiAautoRescanBody request) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String username = clientType.name().toUpperCase() + "/" + user.getUsername();
        final CpeParamListWS cpeParamListWS = new CpeParamListWS();

        CpeParamWS cpeParamWS = new CpeParamWS();
        cpeParamWS.setValue("0");
        cpeParamWS.setName(request.getAutChannelParamName());
        cpeParamWS.setReprovision(false);
        cpeParamListWS.getCPEParam().add(cpeParamWS);

        cpeParamWS = new CpeParamWS();
        cpeParamWS.setValue("1");
        cpeParamWS.setName(request.getAutChannelParamName());
        cpeParamWS.setReprovision(false);
        cpeParamListWS.getCPEParam().add(cpeParamWS);

//        final IntegerArrayWS deviceIds = new IntegerArrayWS();
//        deviceIds.getId().add(request.getDeviceId().intValue());
//        try {
//            final TransactionIdResponse response = AcsProvider.getAcsWebService(clientType)
//                    .setCPEParams(deviceIds, cpeParamListWS, 2, true,
//                            true, false, null, username);
//            final String note = cpeParamListWS.getCPEParam()
//                    .stream()
//                    .map(p -> p.getName() + "=" + p.getValue())
//                    .collect(Collectors.joining("; "));
//            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
//                    .userId(session.getUserId())
//                    .clientType(clientType)
//                    .activityType(DeviceActivityType.Change_parameters)
//                    .deviceId(deviceId)
//                    .serial(serial)
//                    .note(note)
//                    .build());
//        } catch (Exception e) {
//
//        }
        return null;
    }


    @Data
    static class Pair {
        private Integer key;
        private String value;

    }


    @Data
    @AllArgsConstructor
    static class ModifyResponse {
        private ModifyOption modifyOption;
        private HashMap<String, Object> map;

    }

    static enum ModifyOption {
        DELETE,
        MODIFY,
        EQUALS
    }

}