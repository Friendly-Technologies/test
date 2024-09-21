package com.friendly.services.management.action.service;

import com.friendly.services.management.action.dto.enums.ActionOwnerTypeEnum;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import com.friendly.services.management.action.dto.request.GetActionListByMonitorIdRequest;
import com.friendly.services.management.action.dto.response.AbstractActionMethodDetailsParameters;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionMethodDetails;
import com.friendly.services.management.action.dto.response.ActionMethods;
import com.friendly.services.management.action.dto.response.ActionMethodsList;
import com.friendly.services.management.action.dto.response.CpeMethodResponse;
import com.friendly.services.management.action.dto.response.MonitorActionResponse;
import com.friendly.services.management.action.dto.response.RpcMethodResponse;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.rpc.RpcMethod;
import com.friendly.commons.models.user.Session;
import com.friendly.services.management.action.mapper.ActionMapper;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.management.action.orm.acs.repository.ActionRepository;
import com.friendly.services.device.template.service.TemplateService;
import com.ftacs.UpdateGroupTaskWSList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.services.management.action.dto.enums.ActionTypeEnum.ACTION_TASK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionService {
    private final ActionMapper actionMapper;
    private final ActionRepository actionRepository;
    private final ProductClassGroupRepository productClassGroupRepository;
    private final TemplateService templateService;
    private final JwtService jwtService;
    private static final List<ActionTypeEnum> ORDER = Arrays.asList(
            ActionTypeEnum.RE_PROVISION_TASK,
            ActionTypeEnum.FACTORY_RESET_TASK,
            ActionTypeEnum.REBOOT_TASK,
            ActionTypeEnum.RPC_METHOD_TASK,
            ActionTypeEnum.CPE_METHOD_TASK
    );

    public UpdateGroupTaskWSList convertActionToUpdateGroupWSList(final List<AbstractActionRequest> requestList, final Long groupId,
                                                                  final ActionOwnerTypeEnum ownerType, final Integer automationId) {
        List<ActionEntity> actionEntities = new ArrayList<>();
        if (requestList == null) {
            actionEntities = actionRepository.getAllByUgIdAndOwnerType(automationId,
                    ownerType.getOwnerType());
        }
        return actionMapper.convertToUpdateGroupTaskWSList(requestList, actionEntities, groupId);
    }

    public UpdateGroupTaskWSList convertActionToUpdateGroupWSList(final List<AbstractActionRequest> requestList, final Long groupId) {
        return actionMapper.convertToUpdateGroupTaskWSList(requestList, groupId);
    }

    public MonitorActionResponse getMonitorActionList(final GetActionListByMonitorIdRequest body, final String token) {
        jwtService.getSession(token);
        Long groupId = productClassGroupRepository.getIdByManufacturerAndModel(body.getManufacturer(), body.getModel());
        return actionMapper.convertToActionResponse(actionRepository.getAllByUgIdAndOwnerType(body.getId(),
                body.getType().getOwnerType()), groupId);
    }

    public List<ActionListResponse> getActionList(Long groupId, Integer ownerId, ActionOwnerTypeEnum ownerType) {
        return actionMapper.convertToActionListResponse(actionRepository.getAllByUgIdAndOwnerType(ownerId,
                ownerType.getOwnerType()), groupId);
    }


    public ActionMethodsList getActionMethods(final String manufacturer, final String model, ActionOwnerTypeEnum ownerType, final String token) {
        Session session = jwtService.getSession(token);
        final List<ActionMethods> responseMethods = new ArrayList<>();
        Optional<ProductClassGroupEntity> opt = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model);
        if (!opt.isPresent()) {
            log.error("Manufacturer {} and model {} don't exist", manufacturer, model);
            return (new ActionMethodsList(Collections.emptyList()));
        }
        ProductClassGroupEntity productClassGroup = opt.get();
        final Long groupId = productClassGroup.getId();
        final ProtocolType protocolType = ProtocolType.fromValue(productClassGroup.getProtocolId() == null ? 0 : productClassGroup.getProtocolId());
        final List<ActionTypeEnum> actionTypeEnums = ActionTypeEnum.getEnums();
        for (ActionTypeEnum e : actionTypeEnums) {
            if (e != null) {
                if (checkAvailableMethodsForOwnerType(ownerType, e)
                        || checkAvailableMethodsBase(e, groupId, protocolType)
                        || checkAvailableMethodsForUSP(e, protocolType, groupId)
                        || checkAvailableMethodsForMQTT(e, protocolType, groupId)
                        || checkAvailableMethodsForTR(e, protocolType, responseMethods, session, groupId)
                        || checkAvailableMethodsForLWM2M(e, protocolType, responseMethods, groupId)
                ) continue;

                responseMethods.add(ActionMethods.builder()
                        .type(e)
                        .description(e.getDescription())
                        .build());
            }
        }
        sortActionTab(responseMethods);
        return new ActionMethodsList(responseMethods);
    }

    private static void sortActionTab(List<ActionMethods> responseMethods) {
        for (ActionMethods actionMethods : responseMethods) {
            if (ACTION_TASK.getDescription().equals(actionMethods.getDescription())) {
                actionMethods.getTypes().sort(Comparator.comparingInt(m -> ORDER.indexOf(m.getType())));
            }
        }
    }

    private boolean checkAvailableMethodsBase(ActionTypeEnum e, Long groupId, ProtocolType protocolType) {
        if (e == ActionTypeEnum.DIAGNOSTIC_TASK && !templateService.isAnyDiagnosticsExists(groupId))
            return true;
        if (!templateService.isParamExistInTemplateLike(groupId, "%.SoftwareModules.%") && (e == ActionTypeEnum.INSTALL_TASK || e == ActionTypeEnum.UNINSTALL_TASK || e == ActionTypeEnum.UPDATE_SOFTWARE_TASK))
            return true;
        return Arrays.asList(ProtocolType.MQTT, ProtocolType.LWM2M, ProtocolType.USP).contains(protocolType) && e == ActionTypeEnum.RPC_METHOD_TASK;
    }

    private boolean checkAvailableMethodsForMQTT(ActionTypeEnum e, ProtocolType protocolType, Long groupId) {
        if (protocolType.equals(ProtocolType.MQTT)) {
            if (e == ActionTypeEnum.GET_TASK || e == ActionTypeEnum.SET_ATTRIBUTES_TASK || e == ActionTypeEnum.UPLOAD_TASK || e == ActionTypeEnum.BACKUP_TASK || e == ActionTypeEnum.RESTORE_TASK)
                return true;
            return e == ActionTypeEnum.DOWNLOAD_TASK && !templateService.isParamExistInTemplateLike(groupId, "Download");
        }
        return false;
    }

    private boolean checkAvailableMethodsForUSP(ActionTypeEnum e, ProtocolType protocolType, Long groupId) {
        if (protocolType.equals(ProtocolType.USP)) {
            if (e == ActionTypeEnum.SET_ATTRIBUTES_TASK)
                return true;
            if ((e == ActionTypeEnum.UPLOAD_TASK || e == ActionTypeEnum.BACKUP_TASK) && !templateService.isParamExistInTemplateLike(groupId, "Device.DeviceInfo.VendorConfigFile.0.Backup()"))
                return true;
            if (e == ActionTypeEnum.DOWNLOAD_TASK && !templateService.isParamExistInTemplateLike(groupId, "Device.DeviceInfo.FirmwareImage.0.Download()"))
                return true;
            return e == ActionTypeEnum.RESTORE_TASK && !templateService.isParamExistInTemplateLike(groupId, "Device.DeviceInfo.VendorConfigFile.0.Restore()");
        }
        return false;
    }

    private static boolean checkAvailableMethodsForOwnerType(ActionOwnerTypeEnum ownerType, ActionTypeEnum e) {
        if ((ownerType == ActionOwnerTypeEnum.UPDATE_GROUP || ownerType == ActionOwnerTypeEnum.PROFILE_AUTOMATION_EVENTS) && e == ActionTypeEnum.ADD_TO_PROVISION_TASK) {
            return true;
        }
        return ownerType.equals(ActionOwnerTypeEnum.UPDATE_GROUP) && e == ActionTypeEnum.CALL_API_TASK;
    }

    private boolean checkAvailableMethodsForLWM2M(ActionTypeEnum e, ProtocolType protocolType, List<ActionMethods> responseMethods, Long groupId) {
        List<AbstractActionMethodDetailsParameters> details = new ArrayList<>();
        if ((e == ActionTypeEnum.BACKUP_TASK || e == ActionTypeEnum.UPLOAD_TASK || e == ActionTypeEnum.RESTORE_TASK || e == ActionTypeEnum.SET_ATTRIBUTES_TASK) && protocolType.equals(ProtocolType.LWM2M)) {
            return true;
        }
        if ((e == ActionTypeEnum.CPE_METHOD_TASK || e == ActionTypeEnum.RE_PROVISION_TASK) && protocolType.equals(ProtocolType.LWM2M)) {
            Optional<ActionMethods> possibleActionMethod = responseMethods.stream()
                    .filter(obj -> obj.getDescription().equals(ACTION_TASK.getDescription()))
                    .findFirst();
            if (possibleActionMethod.isPresent()) {
                if (e == ActionTypeEnum.CPE_METHOD_TASK) {

                    templateService.getMethodNameEntities(groupId)
                            .forEach(method -> {
                                if (method.contains("Root.")) {
                                    details.add(CpeMethodResponse.builder()
                                            .method(method)
                                            .instance(getInstancesForCpeMethod(method.toLowerCase()))
                                            .name(parseMethodsName(method))
                                            .build());
                                }
                            });
                    possibleActionMethod.get()
                            .getTypes()
                            .add(ActionMethodDetails.builder()
                                    .type(e)
                                    .details(details)
                                    .build());
                } else {
                    possibleActionMethod
                            .get()
                            .getTypes()
                            .add(ActionMethodDetails.builder()
                                    .type(e)
                                    .description(e.getDescription())
                                    .build());
                }
            } else {
                if (e == ActionTypeEnum.CPE_METHOD_TASK) {
                    templateService.getMethodNameEntities(groupId)
                            .forEach(method -> {
                                if (method.contains("Root.")) {
                                    details.add(CpeMethodResponse.builder()
                                            .method(method)
                                            .instance(getInstancesForCpeMethod(method.toLowerCase()))
                                            .name(parseMethodsName(method))
                                            .build());
                                }
                            });

                    responseMethods.add(ActionMethods.builder()
                            .description(ACTION_TASK.getDescription())
                            .type(ACTION_TASK)
                            .types(Collections.singletonList(ActionMethodDetails.builder()
                                    .type(e)
                                    .details(details)
                                    .build()))
                            .build());
                } else {
                    List<ActionMethodDetails> types = new ArrayList<>();
                    types.add(ActionMethodDetails.builder()
                            .type(e)
                            .description(e.getDescription())
                            .build());
                    responseMethods.add(ActionMethods.builder()
                            .description(ACTION_TASK.getDescription())
                            .type(ACTION_TASK)
                            .types(types)
                            .build());
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkAvailableMethodsForTR(ActionTypeEnum e, ProtocolType protocolType, List<ActionMethods> responseMethods, Session session, Long groupId) {
        if ((e == ActionTypeEnum.RPC_METHOD_TASK
                || e == ActionTypeEnum.RE_PROVISION_TASK
                || e == ActionTypeEnum.FACTORY_RESET_TASK
                || e == ActionTypeEnum.REBOOT_TASK) && protocolType.equals(ProtocolType.TR069)) {
            Optional<ActionMethods> possibleActionMethod = responseMethods.stream()
                    .filter(obj -> obj.getDescription().equals(ACTION_TASK.getDescription()))
                    .findFirst();
            if (possibleActionMethod.isPresent()) {
                if (e == ActionTypeEnum.RPC_METHOD_TASK) {
                    List<RpcMethod> methodsForDeviceRoot = DeviceUtils.getRpcMethods(session.getClientType(), templateService.getRootParamName(groupId));
                    possibleActionMethod.get()
                            .getTypes()
                            .add(ActionMethodDetails.builder()
                                    .type(e)
                                    .details(methodsForDeviceRoot
                                            .stream()
                                            .map(o -> RpcMethodResponse.builder()
                                                    .method(o.getMethod())
                                                    .request(o.getRequest())
                                                    .build()).collect(Collectors.toList()))
                                    .build());
                } else {
                    possibleActionMethod
                            .get()
                            .getTypes()
                            .add(ActionMethodDetails.builder()
                                    .type(e)
                                    .description(e.getDescription())
                                    .build());
                }
            } else {
                if (e == ActionTypeEnum.RPC_METHOD_TASK) {
                    List<RpcMethod> methodsForDeviceRoot = DeviceUtils.getRpcMethods(session.getClientType(), templateService.getRootParamName(groupId));
                    List<ActionMethodDetails> types = new ArrayList<>();
                    types.add(ActionMethodDetails.builder()
                            .type(e)
                            .details(methodsForDeviceRoot
                                    .stream()
                                    .map(o -> RpcMethodResponse.builder()
                                            .method(o.getMethod())
                                            .request(o.getRequest())
                                            .build()).collect(Collectors.toList()))
                            .build());

                    responseMethods.add(ActionMethods.builder()
                            .description("Action")
                            .type(ACTION_TASK)
                            .types(types)
                            .build());
                } else {
                    List<ActionMethodDetails> types = new ArrayList<>();
                    types.add(ActionMethodDetails.builder()
                            .type(e)
                            .description(e.getDescription())
                            .build());
                    responseMethods.add(ActionMethods.builder()
                            .description("Action")
                            .type(ACTION_TASK)
                            .types(types)
                            .build());
                }
            }
            return true;
        }
        return false;
    }

    private String parseMethodsName(String method) {
        if (method.contains("IPSO_")) {
            method = method.substring("Root.IPSO_".length());
        } else {
            method = method.substring("Root.".length());
        }
        return removeZeroValueFromMethodName(method);
    }

    private String removeZeroValueFromMethodName(String method) {
        String methodBeforeZero = method.substring(0, method.indexOf(".") + 1);
        String splitMethodNameWithZero = method.substring(methodBeforeZero.length());
        String methodAfterZero = splitMethodNameWithZero.substring(splitMethodNameWithZero.indexOf(".") + 1, splitMethodNameWithZero.length());
        return methodBeforeZero + methodAfterZero;
    }

    private Integer getInstancesForCpeMethod(String input) {
        Integer instance = 0;
        if (input.contains("lwm2m")) {
            int firstSpaceIndex = input.indexOf(' ');
            input = input.substring(firstSpaceIndex + 1);
        }
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                instance = Integer.parseInt(c + "");
            }
        }
        return instance;
    }

    public Boolean isExistActionsByUgId(final Integer id, final ActionOwnerTypeEnum ownerType) {
        return actionRepository.existsActionEntityByUgIdAndOwnerType(id, ownerType.getOwnerType());
    }
}
