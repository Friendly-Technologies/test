package com.friendly.services.device.info.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.ProductClassGroup;
import com.friendly.commons.models.device.response.TaskKeyResponse;
import com.friendly.commons.models.device.software.SoftwareInstallRequest;
import com.friendly.commons.models.device.software.SoftwareUnInstallRequests;
import com.friendly.commons.models.device.software.SoftwareUpdateRequest;
import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.activity.service.TaskService;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.info.model.DeploymentUnitDetails;
import com.friendly.services.device.info.model.DeploymentUnitDetailsList;
import com.friendly.services.device.template.orm.acs.model.DeviceTemplateEntity;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.UpdateOrUninstalOpDetailsForTask;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.ftacs.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.DeviceActivityType.DEPLOYMENT_UNIT;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceSoftwareService {

    @NonNull
    private final StatisticService statisticService;


    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final TaskService taskService;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final UserService userService;

    @NonNull
    private final ParameterService parameterService;

    @NonNull
    private final TemplateService templateService;

    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;



    public DeviceTab getSoftwareTab(final Long deviceId) {
        return parameterService.isParamExistLike(deviceId, "%.SoftwareModules.%") ? DeviceTab.builder()
                .name("Software manager")
                .path("software-manager")
                .build()
                : null;
    }

    public TaskKeyResponse install(final String token, final SoftwareInstallRequest request) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS cpeList = new IntegerArrayWS();
        cpeList.getId().add(request.getDeviceId() != null ? request.getDeviceId().intValue() : null);

        final InstallOpListWS listWS = new InstallOpListWS();
        final InstallOpStructWS structWS = new InstallOpStructWS();
        final String url = request.getLink() == null && request.getFileName() != null && !request.getUrl().endsWith(request.getFileName()) ?
                request.getUrl().endsWith("/") ? request.getUrl() + request.getFileName()
                        : request.getUrl() + "/" + request.getFileName()
                : request.getLink();
        structWS.setPassword(request.getPassword());
        structWS.setReprovision(request.getReprovision());
        structWS.setUrl(url);
        structWS.setUsername(request.getUsername());
        structWS.setUuid(CommonUtils.ACS_OBJECT_FACTORY.createInstallOpStructWSUuid(request.getUuid()));

        listWS.getInstallOperation().add(structWS);
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(request.getDeviceId());
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .changeDUState(cpeList, listWS, new UpdateOpListWS(), new UnInstallOpListWS(),
                                    request.getPriority(), request.getPush() != null && request.getPush(),
                                    request.getResetSession() != null && request.getResetSession(), null, creator);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(user.getId())
                    .clientType(clientType)
                    .activityType(DEPLOYMENT_UNIT)
                    .deviceId(request.getDeviceId())
                    .groupId(groupId)
                    .note("operation=install" +
                            " url=" + request.getUrl())
                    .serial(serial)
                    .build());
            return new TaskKeyResponse(taskService.getTaskKey(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public TaskKeyResponse unInstall(final String token, final SoftwareUnInstallRequests request) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS cpeList = new IntegerArrayWS();
        cpeList.getId().add(request.getDeviceId().intValue());

        final UnInstallOpListWS listWS = new UnInstallOpListWS();

        listWS.getUnInstallOperation().addAll(request.getItems().stream().map(softwareUnInstallRequest -> {
            final UnInstallOpStructWS structWS1 = new UnInstallOpStructWS();
            structWS1.setVersion(softwareUnInstallRequest.getVersion());
            structWS1.setUuid(softwareUnInstallRequest.getUuid());
            return structWS1;
        }).collect(Collectors.toList()));

        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .changeDUState(cpeList, new InstallOpListWS(), new UpdateOpListWS(), listWS,
                                    request.getItems().get(0).getPriority(), request.getPush() != null && request.getPush(),
                                    request.getItems().get(0).getResetSession() != null && request.getItems().get(0).getResetSession(), null, creator);
            request.getItems().forEach(req -> {
                CpeSerialProtocolIdGroupIdProjection cpeProjection =
                        cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(request.getDeviceId());
                final String serial = cpeProjection.getSerial();
                final Long groupId = cpeProjection.getGroupId();
                        statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                                .userId(user.getId())
                                .clientType(clientType)
                                .activityType(DEPLOYMENT_UNIT)
                                .deviceId(request.getDeviceId())
                                .groupId(groupId)
                                .note("operation=uninstall" +
                                        " uuid=" + req.getUuid())
                                .serial(serial)
                                .build());
                    }
            );

            return new TaskKeyResponse(taskService.getTaskKey(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }

    }



    public TaskKeyResponse update(final String token, final SoftwareUpdateRequest request) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS cpeList = new IntegerArrayWS();
        cpeList.getId().add(request.getDeviceId() != null ? request.getDeviceId().intValue() : null);

        final UpdateOpListWS listWS = new UpdateOpListWS();
        final UpdateOpStructWS structWS = new UpdateOpStructWS();

        final String url = request.getLink() == null && request.getFileName() != null && !request.getUrl().endsWith(request.getFileName()) ?
                request.getUrl().endsWith("/") ? request.getUrl() + request.getFileName()
                        : request.getUrl() + "/" + request.getFileName()
                : request.getLink();

        structWS.setVersion(request.getVersion());
        structWS.setUuid(request.getUuid());
        structWS.setPassword(request.getPassword());
        structWS.setUrl(url);
        structWS.setUsername(request.getUsername());

        listWS.getUpdateOperation().add(structWS);
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(request.getDeviceId());
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .changeDUState(cpeList, new InstallOpListWS(), listWS, new UnInstallOpListWS(),
                                    request.getPriority(), request.getPush() != null && request.getPush(),
                                    request.getResetSession() != null && request.getResetSession(), null, creator);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(user.getId())
                    .clientType(clientType)
                    .activityType(DEPLOYMENT_UNIT)
                    .deviceId(request.getDeviceId())
                    .groupId(groupId)
                    .note("operation=update" +
                            " uuid=" + request.getUuid())
                    .serial(serial)
                    .build());
            return new TaskKeyResponse(taskService.getTaskKey(response.getTransactionId()));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public DeploymentUnitDetailsList getDeviceSoftwareDetails(String token, ProductClassGroup request) {
        jwtService.getSession(token);
        Long groupId = productClassGroupRepository.getIdByManufacturerAndModel(request.getManufacturer(), request.getModel());
        Map<Integer, DeploymentUnitDetails> deploymentUnitParamsMap = new HashMap<>();
        List<DeviceTemplateEntity>  deploymentUnitParamsList = (List<DeviceTemplateEntity>) templateService.findAllByOwnerIdAndFullNameLike(groupId, "%Device.SoftwareModules.DeploymentUnit.%");
        final String regex = "InternetGatewayDevice\\.SoftwareModules\\.DeploymentUnit\\.\\d+\\.$";
        final Pattern pattern = Pattern.compile(regex);
        List<String> rootDeploymentUnitsNames = new ArrayList<>();
        for (DeviceTemplateEntity parameter : deploymentUnitParamsList) {
            if (pattern.matcher(parameter.getParameterName().getName()).matches()) {
                rootDeploymentUnitsNames.add(parameter.getParameterName().getName());
            }
        }
        rootDeploymentUnitsNames.forEach(name -> {
            String cutName = name.substring(0,name.lastIndexOf("."));
            Integer instance = Integer.parseInt(cutName.substring(cutName.lastIndexOf(".") + 1));
            Map<String, String> deploymentUnitByInstance = new HashMap<>();
            deploymentUnitParamsList.forEach(param -> {
                if(param.getParameterName().getName().contains(name)) {
                    deploymentUnitByInstance.putIfAbsent(param.getParameterName().getName(), param.getValue());
                }
            });
            DeploymentUnitDetails deploymentUnitDetails = new DeploymentUnitDetails();
            deploymentUnitByInstance.forEach((k, v) -> {
                if(k.endsWith("Name")){
                    deploymentUnitDetails.setDeploymentUnitName(v);
                }
                if(k.endsWith("Version")){
                    deploymentUnitDetails.setVersion(v);
                }
                if(k.endsWith("Description")){
                    deploymentUnitDetails.setDescription(v);
                }
                if(k.endsWith("Status")){
                    deploymentUnitDetails.setStatus(v);
                }
                if(k.endsWith("UUID")){
                    deploymentUnitDetails.setUuid(v);
                }
                deploymentUnitParamsMap.put(instance, deploymentUnitDetails);
            });
        });
        return DeploymentUnitDetailsList.builder()
                .items(new ArrayList<>(deploymentUnitParamsMap.values()))
                .build();
    }

    public UpdateOrUninstalOpDetailsForTask getBranchFromTreeByUUID(final Long groupId, final String uuid) {
        List<DeviceTemplateEntity>  deploymentUnitParamsList = (List<DeviceTemplateEntity>) templateService.findAllByOwnerIdAndFullNameLike(groupId, "%Device.SoftwareModules.DeploymentUnit.%");
        final String regex = "InternetGatewayDevice\\.SoftwareModules\\.DeploymentUnit\\.\\d+\\.UUID$";
        final Pattern pattern = Pattern.compile(regex);
        String uuidParameterNameWithInstance = "";
        for (DeviceTemplateEntity parameter : deploymentUnitParamsList) {
            if (pattern.matcher(parameter.getParameterName().getName()).matches() && parameter.getValue().equals(uuid)) {
                uuidParameterNameWithInstance = parameter.getParameterName().getName();
            }
        }
        final String cutNameForBranch = uuidParameterNameWithInstance.substring(0,uuidParameterNameWithInstance.lastIndexOf("."));
        List<DeviceTemplateEntity> filteredDeviceTemplateEntities = deploymentUnitParamsList
                .stream()
                .filter(p -> p.getParameterName().getName().contains(cutNameForBranch))
                .collect(Collectors.toList());
        UpdateOrUninstalOpDetailsForTask updateOrUninstalOpDetailsForTask = new UpdateOrUninstalOpDetailsForTask();
        filteredDeviceTemplateEntities.forEach(param -> {
            if(param.getParameterName().getName().endsWith("Version")){
                updateOrUninstalOpDetailsForTask.setVersion(param.getValue());
            }
            if(param.getParameterName().getName().endsWith("Name")){
                updateOrUninstalOpDetailsForTask.setNameId(param.getNameId());
                updateOrUninstalOpDetailsForTask.setName(param.getValue());

            }
        });
        return updateOrUninstalOpDetailsForTask;
    }
}
