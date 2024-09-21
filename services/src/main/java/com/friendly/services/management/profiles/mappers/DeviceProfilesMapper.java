package com.friendly.services.management.profiles.mappers;

import com.dm.friendly.ProfileBackup;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.DeliveryProtocolType;
import com.friendly.commons.models.device.file.FileDownloadRequest;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.device.info.utils.DeviceViewUtil;
import com.friendly.services.filemanagement.service.DeviceFileService;
import com.friendly.services.management.profiles.ConditionGroupType;
import com.friendly.services.management.profiles.orm.acs.model.GroupConditionEntity;
import com.friendly.services.management.profiles.orm.acs.model.GroupConditionFilterEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileEventMonitorEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileFileEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterAccessEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterMonitorEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterNotificationEntity;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.management.action.orm.acs.repository.ActionRepository;
import com.friendly.services.management.profiles.orm.acs.repository.DeviceProfileRepository;
import com.friendly.services.management.profiles.entity.GroupConditionFilterItem;
import com.friendly.services.management.profiles.entity.GroupConditionItem;
import com.friendly.services.management.profiles.entity.SimpleCondition;
import com.friendly.services.management.action.dto.enums.ActionOwnerTypeEnum;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfile;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileDetailRequest;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileDetailResponse;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileDownloadFileDetailResponse;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileAutomationEventsRequest;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileAutomationEventsResponse;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileNotificationAccess;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileParameter;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileAutomationParametersRequest;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileAutomationParameterResponse;
import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileAccessEnum;
import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileConditionNameEnum;
import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileNotificationEnum;
import com.friendly.services.management.action.service.ActionService;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.ftacs.CustDeviceFieldListWS;
import com.ftacs.CustDeviceFieldWS;
import com.ftacs.ParameterListWS;
import com.ftacs.ParameterWS;
import com.ftacs.ProfileConditionListWS;
import com.ftacs.ProfileConditionWS;
import com.ftacs.ProfileEventMonitorListWS;
import com.ftacs.ProfileEventMonitorWS;
import com.ftacs.ProfileFileListWS;
import com.ftacs.ProfileFileWS;
import com.ftacs.ProfileListWS;
import com.ftacs.ProfileParameterAccessListWS;
import com.ftacs.ProfileParameterAccessWS;
import com.ftacs.ProfileParameterListWS;
import com.ftacs.ProfileParameterMonitorListWS;
import com.ftacs.ProfileParameterMonitorWS;
import com.ftacs.ProfileParameterNotificationListWS;
import com.ftacs.ProfileParameterNotificationWS;
import com.ftacs.ProfileParameterWS;
import com.ftacs.ProfileWS;
import com.ftacs.ProfileWithIdListWS;
import com.ftacs.ProfileWithIdWS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeviceProfilesMapper {
    private final DomainService domainService;
    private final DeviceProfileRepository deviceProfileRepository;
    private final DeviceMapper deviceMapper;
    private final ActionService actionService;
    private final ActionRepository actionRepository;
    private final DeviceFileService deviceFileService;
    private final ProductClassGroupRepository productClassGroupRepository;


    public DeviceProfile convertToDeviceProfile(Object[] objects, Session session, UserResponse user) {
        DeviceProfileEntity dpe = (DeviceProfileEntity) objects[0];
        ClientType clientType = session.getClientType();
        String zoneId = session.getZoneId();
        return DeviceProfile.builder()
                .id(dpe.getId())
                .domainName(domainService.getDomainNameById(dpe.getDomainId()))
                .status(dpe.getStatus())
                .version(dpe.getVersion())
                .name(dpe.getName())
                .model((String) objects[1])
                .manufacturer((String) objects[2])
                .createdIso(DateTimeUtils.serverToUtc(dpe.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(dpe.getCreated(), clientType, zoneId,
                        user.getDateFormat(), user.getTimeFormat()))
                .creator(dpe.getCreator())
                .application(clientType == ClientType.mc ? "MP" : "SP")
                .build();
    }

    public DeviceProfileDetailResponse convertToDeviceProfileDetail(final DeviceProfileEntity dpe,
                                                                    final List<DeviceProfileParameter> parameters,
                                                                    final List<DeviceProfileNotificationAccess> policy,
                                                                    final List<DeviceProfileAutomationEventsResponse> eventMonitors,
                                                                    final List<DeviceProfileAutomationParameterResponse> parameterMonitors,
                                                                    final List<DeviceProfileDownloadFileDetailResponse> files,
                                                                    final ClientType clientType, final String zoneId,
                                                                    final String dateFormat, final String timeFormat) {
        final Boolean backupInfo = deviceProfileRepository.getProfileBackupInfo(dpe.getId());
        boolean sendBackup = false;
        boolean sendForNewDeviceOnly = false;
        if (backupInfo != null) {
            sendBackup = true;
            sendForNewDeviceOnly = backupInfo;
        }
        return DeviceProfileDetailResponse.builder()
                .id(dpe.getId())
                .name(dpe.getName())
                .model(dpe.getProductClassGroup().getModel())
                .manufacturer(dpe.getProductClassGroup().getManufacturerName())
                .dataTree(dpe.getRoot() != null && dpe.getRoot().equals(1))
                .sendBackup(sendBackup)
                .sendBackupForNewDevicesOnly(sendForNewDeviceOnly)
                .conditionId(dpe.getFilterId())
                .reprovision(dpe.getSendProvision())
                .parameters(parameters)
                .automationParameters(parameterMonitors)
                .automationEvents(eventMonitors)
                .policy(policy)
                .createdIso(DateTimeUtils.serverToUtc(dpe.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(dpe.getCreated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .files(files)
                .version(dpe.getVersion())
                .build();
    }

    public List<DeviceProfileParameter> convertToDeviceProfileParameterList(List<DeviceProfileParameterEntity> parameters) {
        return parameters.stream().map(p ->
                DeviceProfileParameter.builder()
                        .fullName(p.getName())
                        .value(p.getValue())
                        .build()).collect(Collectors.toList());
    }

    public List<DeviceProfileDownloadFileDetailResponse> convertToDeviceFileList(final List<DeviceProfileFileEntity> files, final ServerDetails serverDetails,
                                                                                 final String zoneId, final ClientType clientType,
                                                                                 final String dateFormat, final String timeFormat) {
        List<DeviceProfileDownloadFileDetailResponse> downloadFileDetails = new ArrayList<>();

        files.forEach(f -> {
            String link = f.getUrl();
            String url = link == null || !link.contains("/") ? "" : link.substring(0, link.lastIndexOf("/") + 1);
            String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);
            downloadFileDetails.add(DeviceProfileDownloadFileDetailResponse.builder()
                    .delay(f.getDelaySeconds())
                    .fileTypeId(f.getFileTypeId())
                    .username(f.getUsername())
                    .password(f.getPassword())
                    .fileSize(f.getFileSize())
                    .deliveryMethod(f.getDeliveryMethod() != null
                            ? deviceFileService.getDeliveryMethodById(Integer.valueOf(f.getDeliveryMethod()))
                            : DeliveryMethodType.NotSet)
                    .deliveryProtocol(f.getDeliveryProtocol() != null
                            ? deviceFileService.getDeliveryProtocolById(Integer.valueOf(f.getDeliveryProtocol()))
                            : DeliveryProtocolType.NotSet)
                    .url(url)
                    .createdIso(DateTimeUtils.serverToUtc(f.getCreated(), clientType))
                    .created(DateTimeUtils.formatAcs(f.getCreated(), clientType, zoneId,
                            dateFormat, timeFormat))
                    .creator(f.getCreator())
                    .link(link)
                    .fileName(fileName)
                    .targetFileName(f.getTargetFileName())
                    .isManual(serverDetails != null && serverDetails.getAddress() != null
                            && !url.contains(serverDetails.getAddress()))

                    .build());
        });
        return downloadFileDetails;
    }


    public List<DeviceProfileNotificationAccess> convertToDeviceProfileNotificationAccessList(List<DeviceProfileParameterNotificationEntity> notifications,
                                                                                              List<DeviceProfileParameterAccessEntity> accesses) {
        Map<String, DeviceProfileParameterNotificationEntity> notificationMap = notifications
                .stream()
                .collect(Collectors.toMap(DeviceProfileParameterNotificationEntity::getName, Function.identity(),
                        (existing, replacement) -> replacement));
        Map<String, DeviceProfileParameterAccessEntity> accessMap = accesses
                .stream()
                .collect(Collectors.toMap(DeviceProfileParameterAccessEntity::getName, Function.identity(),
                        (existing, replacement) -> replacement));

        Set<String> allNames = new HashSet<>();
        allNames.addAll(notificationMap.keySet());
        allNames.addAll(accessMap.keySet());

        return allNames
                .stream()
                .map(name -> {
                    DeviceProfileParameterAccessEntity accessEntity = accessMap.get(name);
                    DeviceProfileParameterNotificationEntity notificationEntity = notificationMap.get(name);

                    DeviceProfileAccessEnum accessList = accessEntity != null ? getAccessEnum(accessEntity.getAccessList()) : DeviceProfileAccessEnum.DEFAULT;
                    DeviceProfileNotificationEnum notification = notificationEntity != null ? DeviceProfileNotificationEnum
                            .getEnumByValue(notificationEntity.getNotification()) : DeviceProfileNotificationEnum.DEFAULT;

                    return DeviceProfileNotificationAccess.builder()
                            .fullName(name)
                            .accessList(accessList)
                            .notification(notification)
                            .build();
                }).collect(Collectors.toList());
    }

    private DeviceProfileAccessEnum getAccessEnum(String value) {
        switch (value) {
            case "AcsOnly":
                return DeviceProfileAccessEnum.ACS_ONLY;
            case "All":
                return DeviceProfileAccessEnum.ALL;
            default:
                return DeviceProfileAccessEnum.DEFAULT;
        }
    }
    public List<DeviceProfileAutomationEventsResponse> convertToDeviceProfileAutomationEventsList(List<DeviceProfileEventMonitorEntity> eventMonitors) {
        return eventMonitors.stream().map(e -> DeviceProfileAutomationEventsResponse.builder()
                .id(e.getId())
                .eventName(e.getCpeLogEventNameEntity().getName())
                .duration(e.getDuration())
                .countOfEvents(e.getDuration() == 1 && e.getQuantity() == 1 ? 1 : e.getQuantity())
                .onEachEvent(e.getDuration() == 1 && e.getQuantity() == 1)
                .isAnyTaskHere(actionService.isExistActionsByUgId(e.getId(), ActionOwnerTypeEnum.PROFILE_AUTOMATION_EVENTS))
                .build()).collect(Collectors.toList());
    }

    public List<DeviceProfileAutomationParameterResponse> convertToDeviceProfileAutomationParametersList(List<DeviceProfileParameterMonitorEntity> parameterMonitors) {
        return parameterMonitors.stream()
                .filter(obj -> obj.getParameterNotification() != null)
                .map(p -> DeviceProfileAutomationParameterResponse
                        .builder()
                        .id(p.getId())
                        .conditionName(DeviceProfileConditionNameEnum.getEnumById(p.getParameterMonitorCondition().getId()))
                        .fullName(p.getParameterNotification().getName())
                        .conditionValue(p.getValue())
                        .isAnyTaskHere(actionService.isExistActionsByUgId(p.getId(), ActionOwnerTypeEnum.PROFILE_AUTOMATION_PARAMETERS))
                        .build()
                ).collect(Collectors.toList());
    }

    public ProfileListWS convertToDeviceProfileListWS(final DeviceProfileDetailRequest body,
                                                      final List<GroupConditionFilterEntity> conditionFilterList,
                                                      final ProfileBackup profileBackup,
                                                      final Long groupId,
                                                      final String version) {
        ProfileWS profileWS = new ProfileWS();
        fillProfileWSEntries(profileWS, body, conditionFilterList, profileBackup, groupId, version);
        profileWS.setProfileEventMonitorList(convertToProfileAutomationEventsWSList(body.getAutomationEvents(), body.getManufacturer(), body.getModel()));
        profileWS.setProfileParameterMonitorList(convertToProfileAutomationParametersListWS(body.getAutomationParameters(), body.getManufacturer(), body.getModel()));

        ProfileListWS profileListWS = new ProfileListWS();
        profileListWS.getProfile().add(profileWS);

        return profileListWS;
    }

    public ProfileWithIdListWS convertToProfileWithIdListWS(final DeviceProfileDetailRequest body,
                                                            final List<GroupConditionFilterEntity> conditionFilterList,
                                                            final ProfileBackup profileBackup, Long groupId) {
        ProfileWithIdWS profileWithIdWS = new ProfileWithIdWS();
        fillProfileWithIdWSEntries(profileWithIdWS, body, conditionFilterList, profileBackup, groupId);
        profileWithIdWS.setProfileEventMonitorList(convertToProfileAutomationEventsWSList(body.getAutomationEvents(), body.getManufacturer(), body.getModel()));
        profileWithIdWS.setProfileParameterMonitorList(convertToProfileAutomationParametersListWS(body.getAutomationParameters(), body.getManufacturer(), body.getModel()));
        profileWithIdWS.setProfileId(body.getId());

        ProfileWithIdListWS profileWithIdListWS = new ProfileWithIdListWS();
        profileWithIdListWS.getProfile().add(profileWithIdWS);


        return profileWithIdListWS;
    }

    public void fillProfileWSEntries(com.dm.friendly.ProfileWS profileWS, final DeviceProfileDetailRequest body,
                                     final List<GroupConditionFilterEntity> conditionFilterList,
                                     final ProfileBackup profileBackup,
                                     final Long groupId,
                                     final String version) {

        profileWS.setProfileBackup(profileBackup);
        profileWS.setFullTree(Boolean.TRUE.equals(body.getDataTree()) ? 1 : 0);
        profileWS.setProductClassGroupId(groupId.intValue());
        profileWS.setProfileFileList(convertToProfileFileListWS(body.getFiles()));
        setProfileParameterNotificationAndAccessLists(body.getPolicy(), profileWS);
        profileWS.setProfileParameterList(convertToProfileParameterListWS(body.getParameters()));
        profileWS.setName(body.getName());
        profileWS.setProfileConditionList(convertToProfileConditionListWS(conditionFilterList));
        profileWS.setSendProvision(body.getReprovision());
        profileWS.setVersion(version);
    }

    public void fillProfileWithIdWSEntries(com.dm.friendly.ProfileWS profileWS, final DeviceProfileDetailRequest body,
                                           final List<GroupConditionFilterEntity> conditionFilterList,
                                           final ProfileBackup profileBackup,
                                           final Long groupId) {

        profileWS.setProfileBackup(profileBackup);
        profileWS.setFullTree(Boolean.TRUE.equals(body.getDataTree()) ? 1 : 0);
        profileWS.setProfileFileList(convertToProfileFileListWS(body.getFiles()));
        profileWS.setProductClassGroupId(groupId.intValue());
        setProfileParameterNotificationAndAccessLists(body.getPolicy(), profileWS);
        profileWS.setProfileParameterList(convertToProfileParameterListWS(body.getParameters()));
        profileWS.setName(body.getName());
        profileWS.setProfileConditionList(convertToProfileConditionListWS(conditionFilterList));
        profileWS.setSendProvision(body.getReprovision());
    }



    private void setProfileParameterNotificationAndAccessLists(final List<DeviceProfileNotificationAccess> policy, final com.dm.friendly.ProfileWS profileWS) {
        ProfileParameterAccessListWS profileParameterAccessListWS = new ProfileParameterAccessListWS();
        ProfileParameterNotificationListWS profileParameterNotificationListWS = new ProfileParameterNotificationListWS();
        policy.forEach(e -> {
            ProfileParameterAccessWS profileParameterAccessWS = new ProfileParameterAccessWS();
            ProfileParameterNotificationWS profileParameterNotificationWS = new ProfileParameterNotificationWS();

            profileParameterAccessWS.setAccessList(e.getAccessList().getDescription());
            profileParameterAccessWS.setName(e.getFullName());

            profileParameterNotificationWS.setNotification(e.getNotification().getValue());
            profileParameterNotificationWS.setName(e.getFullName());

            profileParameterAccessListWS.getProfileParameterAccess().add(profileParameterAccessWS);
            profileParameterNotificationListWS.getProfileParameterNotification().add(profileParameterNotificationWS);
        });

        profileWS.setProfileParameterAccessList(profileParameterAccessListWS);
        profileWS.setProfileParameterNotificationList(profileParameterNotificationListWS);
    }

    public ProfileConditionListWS convertToProfileConditionListWS(List<GroupConditionFilterEntity> conditionFilterList) {
        CustDeviceFieldListWS custDeviceFieldListWS = new CustDeviceFieldListWS();
        ParameterListWS parameterListWS = new ParameterListWS();
        for (GroupConditionFilterEntity conditionFilter : conditionFilterList) {
            switch (conditionFilter.getGroupType()) {
                case Inform:
                    ParameterWS parameterWS = getInformParametersWS(conditionFilter);
                    parameterListWS.getParameter().add(parameterWS);
                    break;
                case UserInfo:
                    CustDeviceFieldWS custDeviceFieldWS = getCustDeviceFieldWS(conditionFilter);
                    custDeviceFieldListWS.getCustDeviceField().add(custDeviceFieldWS);
                    break;
                default:
                    break;
            }
        }
        ProfileConditionListWS profileConditionListWS = new ProfileConditionListWS();
        ProfileConditionWS profileConditionWS = new ProfileConditionWS();
        profileConditionWS.setCustDeviceFieldList(custDeviceFieldListWS);
        profileConditionWS.setParameterList(parameterListWS);

        profileConditionListWS.getProfileCondition().add(profileConditionWS);
        return profileConditionListWS;
    }

    private static CustDeviceFieldWS getCustDeviceFieldWS(GroupConditionFilterEntity conditionFilter) {
        CustDeviceFieldWS custDeviceFieldWS = new CustDeviceFieldWS();
        switch (conditionFilter.getType()) {
            case Equal:
                custDeviceFieldWS.setCondition("EQUAL");
                break;
            case NotEqual:
                custDeviceFieldWS.setCondition("NOT_EQUAL");
                break;
            case IsNull:
                custDeviceFieldWS.setCondition("IS_NULL");
                break;
            case IsNotNull:
                custDeviceFieldWS.setCondition("IS_NOT_NULL");
                break;
            case Regexp:
                custDeviceFieldWS.setCondition("REGEXP");
                break;
            default:
                break;
        }

        custDeviceFieldWS.setField(conditionFilter.getName());
        custDeviceFieldWS.setValue(conditionFilter.getValue());
        return custDeviceFieldWS;
    }

    private static ParameterWS getInformParametersWS(GroupConditionFilterEntity conditionFilter) {
        ParameterWS parameterWS = new ParameterWS();
        switch (conditionFilter.getType()) {
            case Equal:
                parameterWS.setCondition("EQUAL");
                break;
            case NotEqual:
                parameterWS.setCondition("NOT_EQUAL");
                break;
            case IsNull:
                parameterWS.setCondition("IS_NULL");
                break;
            case IsNotNull:
                parameterWS.setCondition("IS_NOT_NULL");
                break;
            case Regexp:
                parameterWS.setCondition("REGEXP");
                break;
            default:
                break;
        }

        parameterWS.setName(conditionFilter.getName());
        parameterWS.setValue(conditionFilter.getValue());
        return parameterWS;
    }

    public ProfileParameterListWS convertToProfileParameterListWS(List<DeviceProfileParameter> parameters) {
        ProfileParameterListWS profileParameterListWS = new ProfileParameterListWS();
        if (!parameters.isEmpty()) {
            parameters.forEach(e -> {
                ProfileParameterWS profileParameterWS = new ProfileParameterWS();
                profileParameterWS.setName(e.getFullName());
                profileParameterWS.setValue(e.getValue());
                profileParameterListWS.getProfileParameter().add(profileParameterWS);
            });
        }
        return profileParameterListWS;
    }

    public ProfileFileListWS convertToProfileFileListWS(List<FileDownloadRequest> files) {
        ProfileFileListWS profileFileListWS = new ProfileFileListWS();
        files.forEach(fileRequest -> {
            ProfileFileWS file = new ProfileFileWS();

            String fileName = fileRequest.getFileName();
            file.setFileName(fileRequest.getDescription());
            file.setFileTypeId(fileRequest.getFileTypeId());
            file.setFileSize(fileRequest.getFileSize() == null ? 0 : fileRequest.getFileSize());
            file.setDelaySeconds(fileRequest.getDelay());
            final String url = fileRequest.getLink() == null && fileName != null && !fileRequest.getUrl().endsWith(fileName) ?
                    fileRequest.getUrl().endsWith("/") ? fileRequest.getUrl() + fileRequest.getFileName()
                            : fileRequest.getUrl() + "/" + fileRequest.getFileName()
                    : fileRequest.getLink();
            file.setUrl(url);
            file.setUsername(fileRequest.getUsername());
            file.setPassword(fileRequest.getPassword());
            file.setReset(fileRequest.getResetSession());
            file.setSuccessURL(fileRequest.getSuccessURL());
            file.setFailureURL(fileRequest.getFailureURL());
            file.setDeliveryMethod(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryMethod(deviceMapper.deliveryMethodToInteger(
                    fileRequest.getDeliveryMethod() != null
                            ? fileRequest.getDeliveryMethod() : DeliveryMethodType.NotSet)));
            file.setDeliveryProtocol(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryProtocol(deviceMapper.deliveryProtocolToInteger(
                    fileRequest.getDeliveryProtocol() != null
                            ? fileRequest.getDeliveryProtocol() : DeliveryProtocolType.NotSet)));
            if (fileRequest.getFileVersion() != null)
                file.setFileVersion(fileRequest.getFileVersion());
            file.setTargetFileName(fileRequest.getTargetFileName());
            if (fileRequest.getNewest() != null)
                file.setNewest(fileRequest.getNewest());
            if (fileRequest.getSendBytes() != null)
                file.setSendBytes(fileRequest.getSendBytes());


            profileFileListWS.getProfileFile().add(file);
        });
        return profileFileListWS;
    }

    public ProfileEventMonitorListWS convertToProfileAutomationEventsWSList(List<DeviceProfileAutomationEventsRequest> eventMonitors,
                                                                            String manufacturer, String model) {
        ProfileEventMonitorListWS profileEventMonitorListWS = new ProfileEventMonitorListWS();
            eventMonitors.forEach(e -> {
                ProfileEventMonitorWS profileEventMonitorWS = new ProfileEventMonitorWS();
                profileEventMonitorWS.setEvent(e.getEventName());
                profileEventMonitorWS.setDuration(Boolean.TRUE.equals(e.getOnEachEvent()) ? 1 : e.getDuration());
                profileEventMonitorWS.setQuantity(Boolean.TRUE.equals(e.getOnEachEvent()) ? 1 : e.getCountOfEvents());
                profileEventMonitorWS.setStatus(e.getStatus());
                profileEventMonitorWS.setTasks(actionService.convertActionToUpdateGroupWSList(e.getActionsRequests(),
                            productClassGroupRepository.getIdByManufacturerAndModel(manufacturer, model),
                        ActionOwnerTypeEnum.PROFILE_AUTOMATION_EVENTS,e.getId()));
                profileEventMonitorListWS.getProfileEventMonitor().add(profileEventMonitorWS);
            });
        return profileEventMonitorListWS;
    }

    public ProfileParameterMonitorListWS convertToProfileAutomationParametersListWS(List<DeviceProfileAutomationParametersRequest> parameterMonitors,
                                                                                    String manufacturer, String model) {
        ProfileParameterMonitorListWS profileParameterMonitorListWS = new ProfileParameterMonitorListWS();
            parameterMonitors.forEach(p -> {
                ProfileParameterMonitorWS profileParameterMonitorWS = new ProfileParameterMonitorWS();
                profileParameterMonitorWS.setCondition(p.getConditionName().name());
                profileParameterMonitorWS.setName(p.getFullName());
                profileParameterMonitorWS.setStatus(p.getStatus());
                profileParameterMonitorWS.setValue(p.getConditionValue());
                    profileParameterMonitorWS.setTasks(actionService.convertActionToUpdateGroupWSList(p.getActionsRequests(),
                            productClassGroupRepository.getIdByManufacturerAndModel(manufacturer, model),
                            ActionOwnerTypeEnum.PROFILE_AUTOMATION_PARAMETERS, p.getId()));
                profileParameterMonitorListWS.getProfileParameterMonitor().add(profileParameterMonitorWS);
            });
        return profileParameterMonitorListWS;
    }


    public SimpleCondition getConditions(GroupConditionEntity entity) {
        return SimpleCondition.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public Collection<DeviceProfile> convertToDeviceProfiles(List<Object[]> profiles, Session session, UserResponse user) {
        return profiles.stream()
                .map(objects -> convertToDeviceProfile(objects, session, user))
                .collect(Collectors.toList());
    }

    public GroupConditionItem convertToViewCondition(GroupConditionEntity conditionEntity, ProductClassGroupEntity productClassGroup, String localeId) {
new GroupConditionItem();
        return GroupConditionItem.builder()
                .name(conditionEntity.getName())
                .id(conditionEntity.getId())
                .manufacturer(productClassGroup.getManufacturerName())
                .model(productClassGroup.getModel())
                .conditions(conditionEntity.getFilters().stream().map(e -> convertToViewCondition(e, localeId)).collect(Collectors.toList()))
                .build();
    }

    public GroupConditionFilterItem convertToViewCondition(GroupConditionFilterEntity entity, String localeId) {
        return GroupConditionFilterItem.builder()
                .columnKey(entity.getName())
                .columnName(entity.getGroupType().equals(ConditionGroupType.UserInfo)
                        ? DeviceViewUtil.getColumnName(entity.getName(), localeId) : entity.getName())
                .compare(entity.getType())
                .compareName(DeviceViewUtil.getConditionForType(localeId, entity.getType()))
                .conditionString(entity.getValue())
                .type(entity.getGroupType())
                .id(entity.getId())
                .build();
    }

    public GroupConditionEntity convertViewToEntity(GroupConditionItem item) {

        return GroupConditionEntity.builder()
                .name(item.getName())
                .id(item.getId())
                .build();
    }

    public GroupConditionFilterEntity convertViewConditionFilterToEntity(GroupConditionFilterItem item, Long groupConditionId) {
        return GroupConditionFilterEntity.builder()
                .groupConditionId(groupConditionId)
                .groupType(item.getType())
                .created(Instant.now())
                .type(item.getCompare())
                .name(item.getColumnKey())
                .value(item.getConditionString())
                .build();
    }
}
