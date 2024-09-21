package com.friendly.services.device.info.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.*;
import com.friendly.commons.models.device.response.DeviceServicesResponse;
import com.friendly.commons.models.device.response.DeviceTraceLogFileResponse;
import com.friendly.commons.models.device.response.DeviceTracesResponse;
import com.friendly.commons.models.device.response.ItemTaskIdsResponse;
import com.friendly.commons.models.device.setting.*;
import com.friendly.commons.models.device.tools.*;
import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.activity.service.TaskService;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.customization.Customization;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.device.trace.orm.acs.model.DeviceTraceLogEntity;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.info.orm.acs.repository.DeviceRepository;
import com.friendly.services.device.trace.orm.acs.repository.DeviceTraceRepository;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.userinterface.InterfaceService;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.ftacs.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendly.commons.models.reports.DeviceActivityType.*;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;

/**
 * Service that exposes the base functionality for interacting with {@link Device} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceToolsService {

    DeviceRepository deviceRepository;
    InterfaceService interfaceService;
    UserService userService;
    CpeRepository cpeRepository;
    ParameterService parameterService;
    StatisticService statisticService;
    DeviceSettingService settingService;
    TaskService taskService;
    DeviceTraceRepository traceRepository;
    JwtService jwtService;

    public void startStopDeviceTracing(final String token, final StartStopTraceBody body) {
        Long deviceId = getDeviceId(body);

        if (deviceId != null) {
            startStopDeviceTrace(token, deviceId, body.getAction());
        } else {
            startStopTracingForUnknownCPE(token, body);
        }
    }

    private Long getDeviceId(StartStopTraceBody body) {
        Long deviceId = body.getDeviceId();

        if (deviceId == null && body.getSerial() != null) {
            deviceId = deviceRepository.findFirstBySerial(body.getSerial())
                    .map(DeviceEntity::getId)
                    .orElse(null);
        }
        return deviceId;
    }


    public boolean startStopDeviceTrace(final String token, final Long deviceId, final ActionType action) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        deviceIds.getId().add(deviceId.intValue());

        if (action.equals(ActionType.START)) {
            final Integer duration = interfaceService.getInterfaceValue(clientType, "DeviceTraceDuration")
                    .map(Integer::parseInt)
                    .orElse(1);
            try {
                AcsProvider.getAcsWebService(clientType).startSOAPTracing(deviceIds, duration);

                statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                        .userId(session.getUserId())
                        .clientType(clientType)
                        .activityType(TRACING)
                        .deviceId(deviceId)
                        .serial(serial)
                        .groupId(groupId)
                        .note("startSOAPTracing")
                        .build());
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        } else {
            try {
                AcsProvider.getAcsWebService(clientType).stopSOAPTracing(deviceIds);

                statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                        .userId(session.getUserId())
                        .clientType(clientType)
                        .activityType(TRACING)
                        .serial(serial)
                        .groupId(groupId)
                        .deviceId(deviceId)
                        .note("stopSOAPTracing")
                        .build());
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        }
        return true;
    }

    public boolean startStopTracingForUnknownCPE(final String token, final StartStopTraceBody body) {

        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final String serial = body.getSerial();
        ActionType action = body.getAction();

        CpesoapTracingListWS cpeList = new CpesoapTracingListWS();
        CpesoapTracingWS tracingWS = new CpesoapTracingWS();
        tracingWS.setSerial(serial);
        cpeList.getCPE().add(tracingWS);

        final Long unknownDeviceId = -123L;
        final Long unknownGroupId = 0L;
        if (action.equals(ActionType.START)) {
            final Integer duration = interfaceService.getInterfaceValue(clientType, "DeviceTraceDuration")
                    .map(Integer::parseInt)
                    .orElse(1);
            try {
                AcsProvider.getAcsWebService(clientType).startSOAPTracingForUnknownCPEs(cpeList, duration);

                statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                        .userId(session.getUserId())
                        .clientType(clientType)
                        .activityType(TRACING)
                        .deviceId(unknownDeviceId)
                        .serial(serial)
                        .groupId(unknownGroupId)
                        .note("startSOAPTracingForUnknownCPEs")
                        .build());
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        } else {
            try {
                AcsProvider.getAcsWebService(clientType).stopSOAPTracingForUnknownCPEs(cpeList);

                statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                        .userId(session.getUserId())
                        .clientType(clientType)
                        .activityType(TRACING)
                        .serial(serial)
                        .groupId(unknownGroupId)
                        .deviceId(unknownDeviceId)
                        .note("stopSOAPTracingForUnknownCPEs")
                        .build());
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        }
        return true;
    }

    public TraceStatus getTraceStatus(final String token, final Long deviceId) {
        jwtService.getSession(token);

        return new TraceStatus(traceRepository.isTraceStarted(deviceId) ? ActionType.START : ActionType.STOP);
    }

    public DeviceTracesResponse getTraceLog(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());

        final List<DeviceTraceLogEntity> traceLogEntities = traceRepository.findAllByDeviceIdOrderByCreated(deviceId);

        return new DeviceTracesResponse(convertEntitiesToDeviceTraces(session, user, traceLogEntities));
    }

    private List<DeviceTrace> convertEntitiesToDeviceTraces(final Session session, final UserResponse user, List<DeviceTraceLogEntity> traceLogEntities) {
        Map<Integer, DeviceTraceLogEntity> prevIdToEntity = traceLogEntities.stream()
                .filter(l -> l.getPreviousId() != null)
                .collect(Collectors.toMap(DeviceTraceLogEntity::getPreviousId,
                        deviceTraceLogEntity -> deviceTraceLogEntity,
                        (existing, replacement) -> existing));

        List<DeviceTraceLogEntity> startSessionList = traceLogEntities.stream()
                .filter(l -> l.getPreviousId() == null).collect(Collectors.toList());
        String traceDate = null;
        List<DeviceTraceLogEntity> currentDay = new ArrayList<>();
        Map<String, List<DeviceTraceLogEntity>> dateToSessionStart = new TreeMap<>();
        for (DeviceTraceLogEntity entity : startSessionList) {
            if (traceDate == null) {
                traceDate = DateTimeUtils.formatAcsWithDate(
                        entity.getCreated(), session.getClientType(), session.getZoneId(), user.getDateFormat());
            }
            String curTraceDate = DateTimeUtils.formatAcsWithDate(
                    entity.getCreated(), session.getClientType(), session.getZoneId(), user.getDateFormat());

            if (!traceDate.equals(curTraceDate)) {
                currentDay.sort((o1, o2) -> o2.getCreated().compareTo(o1.getCreated()));
                dateToSessionStart.put(traceDate, currentDay);
                currentDay = new ArrayList<>();
                traceDate = curTraceDate;
            }
            currentDay.add(entity);
        }
        if (traceDate != null) {
            currentDay.sort((o1, o2) -> o2.getCreated().compareTo(o1.getCreated()));
            dateToSessionStart.put(traceDate, currentDay);
        }
        List<DeviceTrace> traceList = new ArrayList<>();
        for (String date : dateToSessionStart.keySet()) {
            List<TraceSession> traceSessions = new ArrayList<>();
            for (DeviceTraceLogEntity entity : dateToSessionStart.get(date)) {
                DeviceTraceLogEntity curEntity = entity;
                List<TraceLog> traceLogs = new ArrayList<>();

                String name = getName(entity.getValue());
                TraceSession traceSession = TraceSession.builder()
                        .date(DateTimeUtils.formatAcsWithDate(
                                curEntity.getCreated(), session.getClientType(), session.getZoneId(), user.getDateFormat()))
                        .name(name + " (" + DateTimeUtils.formatAcsWithTime(
                                curEntity.getCreated(), session.getClientType(), session.getZoneId(), user.getTimeFormat()) + ")")
                        .logs(traceLogs)
                        .build();

                TraceLog traceLog = null;
                do {
                    if (curEntity.getValue().startsWith("</pre>") || curEntity.getValue().startsWith("<center>") || curEntity.getValue().startsWith("<b>")) {
                        name = getName(curEntity.getValue());
                        String message = getMessage(curEntity.getValue());
                        if (curEntity.getValue().startsWith("</pre>")) {
                            name = getName(message);
                            message = getMessage(message);
                        } else if (curEntity.getValue().startsWith("<center>")) {
                            name += " " + DateTimeUtils.formatAcs(
                                    curEntity.getCreated(), session.getClientType(), null,
                                    user.getDateFormat(), user.getTimeFormat());
                            message = null;
                        }
                        traceLog = TraceLog.builder()
                                .id(curEntity.getId())
                                .name(name)
                                .message(message)
                                .created(curEntity.getCreated())
                                .previousId(curEntity.getPreviousId())
                                .build();
                        traceLogs.add(traceLog);
                    } else if (traceLog != null) {
                        traceLog.setMessage(traceLog.getMessage() + curEntity.getValue());
                    }
                    curEntity = prevIdToEntity.get(curEntity.getId());
                } while (curEntity != null);
                traceSessions.add(traceSession);
            }
            traceList.add(DeviceTrace.builder()
                    .date(date)
                    .sessions(traceSessions)
                    .build());
        }
        return traceList;
    }

    public DeviceTraceLogFileResponse getTraceLogTxt(final String token, final TraceTxtBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final Long deviceId = body.getDeviceId();
        final String serial = cpeRepository.getSerial(deviceId);

        final List<DeviceTraceLogEntity> traceLogEntities =
                traceRepository.findAllByDeviceIdAndIdInOrderById(deviceId, body.getLogIds());

        final List<TraceSession> traceSessions = getTraceSessions(session, user, traceLogEntities);
        final StringBuilder stringBuilder =
                new StringBuilder("Trace of device: Serial = ").append(serial)
                        .append(" / ID = ")
                        .append(deviceId)
                        .append("\n\n");
        traceSessions.forEach(s -> {
            stringBuilder.append(s.getName());
            s.getLogs().forEach(t -> {
                stringBuilder.append("\n------------------------------\n")
                        .append(t.getName())
                        .append("\n------------------------------\n");
                if (StringUtils.isNotBlank(t.getMessage())) {
                    final String message = t.getMessage()
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("</pre><hr><pre>", "");
                    stringBuilder.append(message);
                }
            });
            stringBuilder.append("========================================\n");
        });

        return new DeviceTraceLogFileResponse(stringBuilder.toString());
    }

    private List<TraceSession> getTraceSessions(final Session session, final UserResponse user,
                                                final List<DeviceTraceLogEntity> traceLogEntities) {
        final List<TraceSession> traceSessions = new ArrayList<>();
        traceLogEntities.stream()
                .map(logEntity -> TraceLog.builder()
                        .id(logEntity.getId())
                        .name(getName(logEntity.getValue()))
                        .message(getMessage(logEntity.getValue()))
                        .created(logEntity.getCreated())
                        .previousId(logEntity.getPreviousId())
                        .build())
                .forEach(t -> fillTraceSession(session, user, traceSessions, t));

        return traceSessions;
    }

    private void fillTraceSession(final Session session, final UserResponse user,
                                  final List<TraceSession> traceSessions, final TraceLog traceLog) {
        if (traceSessions != null) {
            if (traceLog.getName() != null && traceLog.getName().contains("SESSION STARTED")) {
                traceSessions.add(TraceSession.builder()
                        .date(DateTimeUtils.formatAcsWithDate(
                                traceLog.getCreated(), session.getClientType(), session.getZoneId(), user.getDateFormat()))
                        .name(traceLog.getName() + " (" + DateTimeUtils.formatAcsWithTime(
                                traceLog.getCreated(), session.getClientType(), session.getZoneId(), user.getTimeFormat()) + ")")
                        .logs(new ArrayList<>())
                        .build());
                traceSessions.get(traceSessions.size() - 1)
                        .getLogs()
                        .add(traceLog.toBuilder()
                                .name(getName(traceLog.getMessage()))
                                .message(getMessage(traceLog.getMessage()))
                                .build());
            } else if (!traceSessions.isEmpty()) {
                if (traceLog.getName() != null && traceLog.getName().contains("SESSION ENDED")) {
                    traceSessions.get(traceSessions.size() - 1)
                            .getLogs()
                            .add(traceLog.toBuilder()
                                    .name(traceLog.getName() + DateTimeUtils.formatAcs(
                                            traceLog.getCreated(), session.getClientType(), null,
                                            user.getDateFormat(), user.getTimeFormat()))
                                    .message(null)
                                    .build());
                } else {
                    traceSessions.get(traceSessions.size() - 1)
                            .getLogs()
                            .add(traceLog);
                }
            }

        }
    }

    @Transactional
    public boolean deleteTrace(final String token, final DeviceTraceBody body) {
        jwtService.getSession(token);

        final long count = traceRepository.deleteAllByDeviceIdAndIdIn(body.getDeviceId(), body.getIds());
        return count != 0L;
    }

    public void pingDevice(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        try {
            AcsProvider.getAcsWebService(clientType).pingCPE(deviceId.intValue());
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public void traceRoute(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        try {
            AcsProvider.getAcsWebService(clientType).tracerouteCPE(deviceId.intValue());
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public String getTraceRouteResult(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        try {
            String result = AcsProvider.getAcsWebService(clientType).getTracerouteCPEResult(deviceId.intValue());
            if (result != null) {
                result = result
                        .replace("<![CDATA[", "")
                        .replace("]]>", "")
                        .replace("EOF", "")
                        .trim();
            }
            return result;
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public String getPingResult(String token, Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        try {
            String result = AcsProvider.getAcsWebService(clientType).getPingCPEResult(deviceId.intValue());
            if (result != null) {
                result = result.replace("<![CDATA[", "");
                result = result.replace("EOF", "");
                result = result.replace("]]>", "");
                result = result.trim();
            }
            return result;
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public ItemTaskIdsResponse rebootDevice(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        deviceIds.getId().add(deviceId.intValue());

        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .rebootCPE(deviceIds, 1, true, null, creator);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(REBOOT_DEVICE)
                    .deviceId(deviceId)
                    .groupId(groupId)
                    .serial(serial)
                    .build());
            return new ItemTaskIdsResponse(taskService.getTaskIds(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public ItemTaskIdsResponse factoryReset(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        deviceIds.getId().add(deviceId.intValue());

        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .factoryReset(deviceIds, 1, true, null, creator);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(RESET_DEVICE)
                    .deviceId(deviceId)
                    .groupId(groupId)
                    .serial(serial)
                    .build());
            return new ItemTaskIdsResponse(taskService.getTaskIds(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public ItemTaskIdsResponse reprovision(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        deviceIds.getId().add(deviceId.intValue());

        final boolean sendCPEProvision =
                interfaceService.getInterfaceValue(clientType, "ReprovisionIndividual")
                        .map(Boolean::parseBoolean)
                        .orElse(true);
        final boolean sendCPEProvisionAttribute =
                interfaceService.getInterfaceValue(clientType, "ReprovisionAttribute")
                        .map(Boolean::parseBoolean)
                        .orElse(true);
        final boolean sendProfile =
                interfaceService.getInterfaceValue(clientType, "ReprovisionProfile")
                        .map(Boolean::parseBoolean)
                        .orElse(true);
        final boolean customRPC =
                interfaceService.getInterfaceValue(clientType, "ReprovisionCustomRPC")
                        .map(Boolean::parseBoolean)
                        .orElse(true);
        final boolean cpeProvisionObject =
                interfaceService.getInterfaceValue(clientType, "ReprovisionDeviceProvisionObject")
                        .map(Boolean::parseBoolean)
                        .orElse(true);
        final boolean cpeFile =
                interfaceService.getInterfaceValue(clientType, "ReprovisionDeviceFile")
                        .map(Boolean::parseBoolean)
                        .orElse(true);

        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .reprovisionCPE(deviceIds, sendProfile, sendCPEProvision, sendCPEProvisionAttribute,
                                    customRPC, cpeProvisionObject, cpeFile, null, creator);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(REPROVISION)
                    .deviceId(deviceId)
                    .serial(serial)
                    .groupId(groupId)
                    .build());

            return new ItemTaskIdsResponse(taskService.getTaskIds(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public DeviceServicesResponse getDeviceServices(final String token, final ProtocolType protocolType) {
        Session session = jwtService.getSession(token);

        if (protocolType == ProtocolType.TR069) {
            List<String> services = Customization.getReplaceServicesForClient(session.getClientType())
                    .stream()
                    .map(ReplaceService::getName)
                    .collect(Collectors.toList());
            return new DeviceServicesResponse(services);
        }

        return new DeviceServicesResponse(Collections.emptyList());
    }

    public ItemTaskIdsResponse replaceDevice(final String token, final ReplaceRequest replaceRequest) {
        final Session session = jwtService.getSession(token);

        final List<ReplaceService> services = Customization.getReplaceServicesForClient(session.getClientType())
                .stream()
                .filter(s -> replaceRequest.getServices()
                        .contains(s.getName()))
                .collect(Collectors.toList());
        final List<DeviceAddObjectRequest> addObjectRequests =
                services.stream()
                        .flatMap(s -> s.getObjects() == null ? Stream.empty() : s.getObjects().stream())
                        .map(o -> DeviceAddObjectRequest.builder()
                                .push(true)
                                .reprovision(false)
                                .objectName(o.getFullName())
                                .parameters(o.getParameters()
                                        .stream()
                                        .map(p -> getCurrentObjectParam(
                                                replaceRequest.getFromId(),
                                                o.getFullName() + p.getShortName()))
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList());
        final DeviceParameterUpdateRequest parameterUpdateRequest =
                DeviceParameterUpdateRequest.builder()
                        .push(true)
                        .reprovision(true)
                        .parameters(services.stream()
                                .flatMap(s -> s.getParameters() == null
                                        ? Stream.empty()
                                        : s.getParameters()
                                        .stream()
                                        .map(DeviceParameterSimple::getFullName)
                                        .map(n -> getCurrentParam(
                                                replaceRequest.getFromId(), n)))
                                .collect(Collectors.toList()))
                        .build();

        final List<Long> taskIds = new ArrayList<>();
        taskIds.addAll(addObjectRequests.stream()
                .flatMap(request -> settingService.addObject(replaceRequest.getToId(),
                                toCorrectRequest(request), session)
                        .stream())
                .collect(Collectors.toList()));

        taskIds.addAll(settingService.updateDeviceParams(replaceRequest.getToId(),
                parameterUpdateRequest, session, true, token));

        return new ItemTaskIdsResponse(taskIds);
    }

    private AddObjectRequest toCorrectRequest(DeviceAddObjectRequest request) {
        return AddObjectRequest.builder()
                .reprovision(request.getReprovision())
                .push(request.getPush())
                .objectName(request.getObjectName())
                .parameters(toCorrectParams(request.getParameters()))
                .build();
    }

    private List<NewObjectParam> toCorrectParams(List<ObjectParameter> parameters) {
        return parameters.stream()
                .map(p -> new NewObjectParam(p.getName(), p.getValue()))
                .collect(Collectors.toList());
    }


    private ObjectParameter getCurrentObjectParam(final Long deviceId, final String param) {
        return ObjectParameter.builder()
                .name(ParameterUtil.getShortName(param))
                .value(parameterService.getParamValue(deviceId, param))
                .build();
    }

    private Parameter getCurrentParam(final Long deviceId, final String param) {
        return Parameter.builder()
                .fullName(param)
                .value(parameterService.getParamValue(deviceId, param))
                .build();
    }

    private String getMessage(final String value) {
        return StringUtils.substringAfter(value, "</b>");
    }

    private String getName(final String value) {
        String s = "";
        if (value.contains("Body&gt;") && !value.contains("SESSION STARTED")) {
            s = value.substring(value.indexOf("Body&gt;") + "Body&gt;".length());
            s = s.substring(0, s.indexOf("&gt;"));
            s = s.trim();
            s = s.contains(":") ? s.substring(s.indexOf(":") + 1) : s;
            s = " - " + s;
            s = s.endsWith("/") ? s.substring(0, s.length() - 1).trim() : s;
            String id = getCwmpId(value);
            if (!id.isEmpty()) {
                s += ", ID = " + id;
            }
        }
        return StringUtils.substringBetween(value, "<b>", "</b>") + s;
    }

    private String getCwmpId(final String value) {
        String s = "";
        if (value.contains("Header&gt;") && !value.contains("SESSION STARTED") && !value.contains("Inform&gt;") && !value.contains("InformResponse&gt;") && value.contains("ID ")) {
            s = value.substring(value.indexOf("Header&gt;") + "Header&gt;".length());
            s = s.substring(s.indexOf("ID "));
            s = s.substring(s.indexOf("&gt;") + "&gt;".length());
            s = s.substring(0, s.indexOf("&lt;"));
            s = s.trim();
        }
        return s;
    }

}
