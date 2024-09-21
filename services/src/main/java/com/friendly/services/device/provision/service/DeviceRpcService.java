package com.friendly.services.device.provision.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AddCustomRpcBody;
import com.friendly.commons.models.device.CustomRpcBody;
import com.friendly.commons.models.device.DeleteCustomRpcBody;
import com.friendly.commons.models.device.InvokeMethodBody;
import com.friendly.commons.models.device.response.ItemTaskIdsResponse;
import com.friendly.commons.models.device.response.RpcMethodsResponse;
import com.friendly.commons.models.device.rpc.CustomRpc;
import com.friendly.commons.models.device.rpc.CustomRpcRequest;
import com.friendly.commons.models.device.rpc.RpcMethod;
import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.device.method.orm.acs.repository.CpeMethodNameRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.activity.service.TaskService;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.device.method.orm.acs.repository.CpeMethodRepository;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.activity.orm.acs.repository.DeviceRpcHistoryRepository;
import com.friendly.services.device.activity.orm.acs.repository.TaskRepository;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.ftacs.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.DeviceActivityType.CUSTOM_RPC;
import static com.friendly.commons.models.reports.DeviceActivityType.INVOKE_METHOD;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRpcService {

    @NonNull
    private final DeviceMapper deviceMapper;

    @NonNull
    private final UserService userService;

    @NonNull
    private final StatisticService statisticService;

    @NonNull
    private final TaskService taskService;

    @NonNull
    private final DeviceRpcHistoryRepository rpcRepository;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final TaskRepository taskRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final CpeMethodRepository cpeMethodRepository;

    private final ParameterService parameterService;

    final CpeMethodNameRepository cpeMethodNameRepository;

    public FTPage<CustomRpc> getCustomRpc(final String token, CustomRpcBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());

        List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");
        final List<Page<Map<String, Object>>> customRpcPage =
                pageable.stream()
                        .map(p -> rpcRepository.getCustomRPCs(body.getDeviceId(), p))
                        .collect(Collectors.toList());
        final List<CustomRpc> customRpcs =
                customRpcPage.stream()
                        .map(Page::getContent)
                        .flatMap(e -> deviceMapper.customRpcEntitiesToCustomRpcs(e,
                                        session.getClientType(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList());

        final FTPage<CustomRpc> result = new FTPage<>();
        return result.toBuilder()
                .items(customRpcs)
                .pageDetails(PageUtils.buildPageDetails(customRpcPage))
                .build();
    }

    public RpcMethodsResponse getRpcMethods(final String token, final Long deviceId) {
        Session session = jwtService.getSession(token);
        List<String> methodNames = cpeMethodRepository.findMethodNamesByCpeId(deviceId).stream()
                .map(s -> s.replaceAll(" ", ""))
                .collect(Collectors.toList());
        List<RpcMethod> methodsForDeviceRoot = DeviceUtils.getRpcMethods(session.getClientType(), getRootParamName(deviceId));
        List<RpcMethod> methodsForDevice = methodsForDeviceRoot.stream()
                .filter(rpcMethod -> methodNames.contains(rpcMethod.getMethod()))
                .sorted(Comparator.comparing(RpcMethod::getMethod))
                .collect(Collectors.toList());
        return new RpcMethodsResponse(methodsForDevice);
    }

    public void invokeMethod(String token, InvokeMethodBody body) {
        Session session = jwtService.getSession(token);

        Integer methodId = cpeMethodNameRepository.getIdByMethodName(body.getMethodName());

        Long deviceId = body.getDeviceId();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        ClientType clientType = session.getClientType();

        final IntegerArrayWS cpeList = new IntegerArrayWS();
        cpeList.getId().add(deviceId != null ? deviceId.intValue() : null);
        final String creator =
                session.getClientType().name().toUpperCase()
                        + "/"
                        + userService.getUser(session.getUserId(), session.getZoneId()).getUsername();

        final ParameterListWS parameterList = new ParameterListWS();
        body.getRequest()
                .getParameters()
                .forEach(
                        param -> {
                            ParameterWS parameterWS = new ParameterWS();
                            parameterWS.setName("execParam");
                            parameterWS.setValue(param.getValue());
                            parameterList.getParameter().add(parameterWS);
                        });

        try {
            AcsProvider.getAcsWebService(session.getClientType())
                    .invokeCpeMethod(cpeList, methodId, null, false, null, creator, parameterList);
            statisticService.addDeviceLogAct(
                    DeviceActivityLog.builder()
                            .userId(session.getUserId())
                            .clientType(clientType)
                            .activityType(INVOKE_METHOD)
                            .deviceId(deviceId)
                            .groupId(groupId)
                            .serial(serial)
                            .note("Invoked method " + body.getMethodName())
                            .build());
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public ItemTaskIdsResponse addCustomRpc(final String token, final AddCustomRpcBody body) {
        final Session session = jwtService.getSession(token);
        final CustomRpcRequest rpc = body.getRpc();
        final Long deviceId = body.getDeviceId();

        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();

        final IntegerArrayWS deviceIds = new IntegerArrayWS();
        deviceIds.getId().add(deviceId.intValue());
        final RpcMethodListWS rpcMethodListWS = new RpcMethodListWS();
        final RpcMethodWS rpcMethodWS = new RpcMethodWS();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        rpcMethodWS.setMethodName(rpc.getMethod());
        rpcMethodWS.setRequestMessage(rpc.getRequest());
        rpcMethodWS.setReprovision(rpc.getReprovision());
        rpcMethodListWS.getRpcMethodWS().add(rpcMethodWS);

        try {
            final InvokeRPCResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .invokeRPCMethod(deviceIds, rpcMethodListWS, 3, rpc.getPush(), null, creator);
            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(CUSTOM_RPC)
                    .serial(serial)
                    .groupId(groupId)
                    .deviceId(deviceId)
                    .note(rpc.getMethod())
                    .build());

            return new ItemTaskIdsResponse(taskService.getTaskIds(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    @Transactional
    public void deleteCustomRpc(final String token, final DeleteCustomRpcBody body) {
        jwtService.getSession(token);
        final Long deviceId = body.getDeviceId();
        final List<Long> taskIds = body.getTaskIds();

        //TODO: change to delete by ACS
        taskIds.forEach(taskId -> {
            rpcRepository.deleteByTaskId(taskId);
            if (taskRepository.deleteCompletedTask(deviceId, taskId) > 0) {
                return;
            }
            if (taskRepository.deletePendingTask(deviceId, taskId) > 0) {
                return;
            }
            if (taskRepository.deleteRejectedTask(deviceId, taskId) > 0) {
                return;
            }
            taskRepository.deleteFailedTask(deviceId, taskId);
        });
    }

    private String getRootParamName(final Long deviceId) {
        return isParamExist(deviceId, "InternetGatewayDevice.%")
                ? "InternetGatewayDevice."
                : isParamExist(deviceId, "Device.%") ? "Device." : "Root.";
    }

    private boolean isParamExist(final Long deviceId, final String param) {
        return parameterService.isParamExistLike(deviceId, param);
    }

}
