package com.friendly.services.management.profiles.service;

import com.dm.friendly.ProfileBackup;
import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.DeviceColumns;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.response.DeviceObjectsResponse;
import com.friendly.commons.models.device.response.DeviceObjectsSimpleResponse;
import com.friendly.commons.models.device.response.DeviceTabsResponse;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.commons.models.tabs.ProfileTabViewBody;
import com.friendly.commons.models.tabs.ProfileTemplateParametersBody;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.view.response.ConditionsResponse;
import com.friendly.commons.models.view.response.DeviceColumnsResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.customization.DeviceTabService;
import com.friendly.services.device.info.utils.ColumnName;
import com.friendly.services.device.info.utils.DeviceViewUtil;
import com.friendly.services.management.profiles.orm.acs.model.GroupConditionEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileEntity;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileFilterEntity;
import com.friendly.services.management.profiles.orm.acs.repository.GroupConditionFilterRepository;
import com.friendly.services.management.profiles.orm.acs.repository.GroupConditionRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.management.profiles.orm.acs.repository.DeviceProfileFilterRepository;
import com.friendly.services.management.profiles.orm.acs.repository.DeviceProfileRepository;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.management.profiles.ConditionGroupType;
import com.friendly.services.management.profiles.entity.GetConditionBody;
import com.friendly.services.management.profiles.entity.GetConditionFiltersBody;
import com.friendly.services.management.profiles.entity.GetConditionsBody;
import com.friendly.services.management.profiles.entity.GetDeviceProfilesBody;
import com.friendly.services.management.profiles.entity.GroupConditionItem;
import com.friendly.services.management.profiles.entity.GroupConditionTypes;
import com.friendly.services.management.profiles.entity.ProfileStatus;
import com.friendly.services.management.profiles.entity.SimpleConditions;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfile;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileBody;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileDetailRequest;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileDetailResponse;
import com.friendly.services.management.profiles.mappers.DeviceProfilesMapper;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.ftacs.Exception_Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DEVICE_PROFILE_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PARAMETER_NOT_UNIQUE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PRODUCT_CLASS_GROUP_NOT_FOUND;
import static com.friendly.services.management.profiles.entity.ProfileStatus.ACTIVE;
import static com.friendly.services.management.profiles.entity.ProfileStatus.NOT_ACTIVE;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceProfileService {
    private final JwtService jwtService;
    private final DeviceProfileRepository deviceProfileRepository;
    private final DeviceProfilesMapper deviceProfileMapper;
    private final UserService userService;
    private final DomainService domainService;
    private final GroupConditionRepository groupConditionRepository;
    private final GroupConditionFilterRepository groupConditionFilterRepository;
    private final ProductClassGroupRepository productClassGroupRepository;
    private final DeviceProfileFilterRepository deviceProfileFilterRepository;
    private final FileServerService fileServerService;
    private final DeviceTabService tabService;
    private final TemplateService templateService;


    public FTPage<DeviceProfile> getProfiles(String token, GetDeviceProfilesBody body) {

        Session session = jwtService.getSession(token);
        UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());

        Integer pageSize = body.getPageSize();
        final int size = pageSize != null && pageSize > 0 ? pageSize : Integer.MAX_VALUE;
        List<Integer> pageNumbers = body.getPageNumbers();
        final List<Pageable> pageables = getPageables(pageNumbers, size);

        return getProfiles(pageables, body.getManufacturer(), body.getModel(), body.getProfileStatus(),
                session, user);
    }

    public FTPage<DeviceProfile> getProfiles(List<Pageable> pageable,
                                             String manufacturer,
                                             String model,
                                             ProfileStatus profileStatus,
                                             Session session,
                                             UserResponse user){
        List<Page<Object[]>> profiles =
                pageable.stream()
                .map(p -> getProfileEntities(p, manufacturer, model, profileStatus))
                .collect(Collectors.toList());

        return getProfilesPage(profiles, session, user);
    }

    private FTPage<DeviceProfile> getProfilesPage(List<Page<Object[]>> profileEntities, Session session, UserResponse user) {
        final List<DeviceProfile> profiles = profileEntities.stream()
                .map(Page::getContent)
                .flatMap(p -> deviceProfileMapper.convertToDeviceProfiles(p, session, user).stream())
                .collect(Collectors.toList());

        return buildProfilePage(profileEntities, profiles);
    }

    private FTPage<DeviceProfile> buildProfilePage(List<Page<Object[]>> profileEntities, List<DeviceProfile> profiles) {
            final FTPage<DeviceProfile> devicePage = new FTPage<>();
            return devicePage.toBuilder()
                    .pageDetails(PageUtils.buildPageDetails(profileEntities))
                    .items(profiles)
                    .build();
    }

    private Page<Object[]> getProfileEntities(Pageable p, String manufacturer, String model, ProfileStatus profileStatus) {
        return deviceProfileRepository.getAllByParams(p, manufacturer, model,
                profileStatus == null ? null : profileStatus.ordinal());
    }

    private List<Pageable> getPageables(final List<Integer> pageNumbers,
                                        final int size) {

        if (pageNumbers == null) {
            return Collections.singletonList(PageRequest.of(0, size));
        } else {
            return pageNumbers.stream()
                    .map(page -> PageRequest.of(page != null && page > 1 ? page - 1 : 0,
                            size))
                    .collect(Collectors.toList());
        }
    }


    public boolean activateDeviceProfile(String token, DeviceProfileBody body) {
        return changeProfileStatus(token, body, ACTIVE);
    }

    public boolean deactivateDeviceProfile(String token, DeviceProfileBody body) {
        return changeProfileStatus(token, body, NOT_ACTIVE);
    }

    private boolean changeProfileStatus(String token, DeviceProfileBody body, ProfileStatus profileStatus) {
        jwtService.getSession(token);
        Optional<DeviceProfileEntity> deviceProfile = deviceProfileRepository.findById(body.getId());

        if(!deviceProfile.isPresent()) {
            return false;
        }

        DeviceProfileEntity deviceProfileEntity = deviceProfile.get();
        deviceProfileEntity.setStatus(profileStatus);
        deviceProfileRepository.save(deviceProfileEntity);
        return true;
    }

    @Transactional
    public boolean deleteDeviceProfile(String token, DeviceProfileBody body) {
        jwtService.getSession(token);

        deviceProfileRepository.deleteById(body.getId());

        return true;
    }

    public SimpleConditions getConditions(String token, GetConditionsBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        List<Integer> domainIds = domainService.getDomainIds(user);
        List<GroupConditionEntity> entities = domainIds == null ?
                groupConditionRepository.findAllByManufacturerAndModel(body.getManufacturer(), body.getModel())
                : groupConditionRepository.findAllByManufacturerAndModel(body.getManufacturer(), body.getModel(), domainIds);

        SimpleConditions resp = new SimpleConditions();
        resp.setItems(entities
                .stream()
                .map(deviceProfileMapper::getConditions)
                .collect(Collectors.toList()));
        return resp;
    }

    public GroupConditionItem getCondition(String token, GetConditionBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        GroupConditionEntity entity = groupConditionRepository.findById(body.getId()).orElse(null);
        if (entity == null) {
            return GroupConditionItem.builder().build();
        }
        return deviceProfileMapper.convertToViewCondition(entity,
                productClassGroupRepository.findById(entity.getGroupId()).orElse(null), user.getLocaleId());

    }

    public GroupConditionTypes getConditionTypes(String token, GetConditionsBody body) {
        jwtService.getSession(token);
        Integer protocolId = productClassGroupRepository.getProtocolIdByManufacturerAndModel(
                body.getManufacturer(), body.getModel());
        protocolId = protocolId == null ? 0 : protocolId;
        ProtocolType protocolType = ProtocolType.fromValue(protocolId);

        return GroupConditionTypes.builder()
                .items(protocolType.equals(ProtocolType.LWM2M) || protocolType.equals(ProtocolType.MQTT) || protocolType.equals(ProtocolType.USP)
                        ? Collections.singletonList(ConditionGroupType.UserInfo.getValue())
                        : ConditionGroupType.getValues())
                .build();
    }

    public DeviceColumnsResponse getConditionFilters(String token, GetConditionFiltersBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        if (body.getConditionType().equals(ConditionGroupType.UserInfo)) {
            List<DeviceColumns> columns = DeviceViewUtil.getGroupConditionFilterColumns()
                    .stream()
                    .map(c -> c.toBuilder()
                            .columnName(DeviceViewUtil.getColumnName(c.getColumnKey(), user.getLocaleId()))
                            .build())
                    .collect(Collectors.toList());
            return new DeviceColumnsResponse(columns);
        }
        Integer protocolId = productClassGroupRepository.getProtocolIdByManufacturerAndModel(
                body.getManufacturer(), body.getModel());
        protocolId = protocolId == null ? 0 : protocolId;
        ProtocolType protocolType = ProtocolType.fromValue(protocolId);
        if (protocolType.equals(ProtocolType.USP)) {
            return new DeviceColumnsResponse(DeviceViewUtil.getGroupConditionInformFilterColumns("Device."));
        } else {
            boolean isGateway = productClassGroupRepository.isParamExistLikeForManufacturerAndModel(
                    body.getManufacturer(), body.getModel(), "InternetGatewayDevice.");
            return new DeviceColumnsResponse(
                    DeviceViewUtil.getGroupConditionInformFilterColumns(isGateway ? "InternetGatewayDevice." : "Device."));
        }
    }

    public ConditionsResponse getConditionComparisonsByColumn(String token, String columnKey) {
        jwtService.getSession(token);
        return new ConditionsResponse(DeviceViewUtil.getGroupConditionComparisonsByType(
                ColumnName.isColumnInNameMap(columnKey) ? ConditionGroupType.UserInfo : ConditionGroupType.Inform));
    }

    public GroupConditionItem saveCondition(String token, GroupConditionItem item) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        GroupConditionEntity entity = deviceProfileMapper.convertViewToEntity(item);
        Instant now = Instant.now();
        if (entity.getId() == null || entity.getId() == 0) {
            entity.setId(null);
            Optional<Long> id = groupConditionRepository.getByNameManufacturerAndModel(item.getName(), item.getManufacturer(), item.getModel());
            if (id.isPresent()) {
                throw new FriendlyIllegalArgumentException(PARAMETER_NOT_UNIQUE, item.getName());
            }
            entity.setCreated(now);
            entity.setDomainId(user.getDomainId());
            entity.setGroupId(productClassGroupRepository.getIdByManufacturerAndModel(item.getManufacturer(), item.getModel()));
        } else {
            entity = groupConditionRepository.findById(entity.getId()).get();
            groupConditionFilterRepository.deleteAllByGroupConditionId(entity.getId());
        }
        entity.setUpdated(now);
        entity = groupConditionRepository.save(entity);
        GroupConditionEntity finalEntity = entity;
        groupConditionFilterRepository.saveAll(item.getConditions().stream()
                .map(i -> deviceProfileMapper.convertViewConditionFilterToEntity(
                        i, finalEntity.getId())).collect(Collectors.toList()));
        entity = groupConditionRepository.findById(entity.getId()).get();
        return deviceProfileMapper.convertToViewCondition(entity,
                productClassGroupRepository.findById(entity.getGroupId()).orElse(null), user.getLocaleId());
    }

    public DeviceProfileDetailResponse getProfileDetails(String token, Integer id) {
        Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        DeviceProfileEntity deviceProfile = deviceProfileRepository.findById(id)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(DEVICE_PROFILE_NOT_FOUND, id));
        return deviceProfileMapper.convertToDeviceProfileDetail(deviceProfile,
                deviceProfileMapper.convertToDeviceProfileParameterList(deviceProfile.getParameters()),
                deviceProfileMapper.convertToDeviceProfileNotificationAccessList(deviceProfile.getNotifications(), deviceProfile.getAccesses()),
                deviceProfileMapper.convertToDeviceProfileAutomationEventsList(deviceProfile.getEventMonitors()),
                deviceProfileMapper.convertToDeviceProfileAutomationParametersList(deviceProfile.getParameterMonitors()),
                deviceProfileMapper.convertToDeviceFileList(deviceProfile.getFiles(), serverDetails, session.getZoneId(), session.getClientType(),
                        user.getDateFormat(), user.getTimeFormat()),
                session.getClientType(), session.getZoneId(), user.getDateFormat(), user.getTimeFormat()
        );
    }

    public DeviceProfileDetailResponse saveOrUpdateProfileDetails(final String token, final DeviceProfileDetailRequest body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final ClientType clientType = session.getClientType();
        final Long groupId = productClassGroupRepository.findByManufacturerNameAndModel(body.getManufacturer(), body.getModel())
                .orElseThrow(() -> new FriendlyEntityNotFoundException(PRODUCT_CLASS_GROUP_NOT_FOUND, body.getManufacturer(), body.getModel())).getId();

        if (body.getId() == null || body.getId() == 0) {
            try {
                AcsProvider.getAcsWebService(clientType)
                        .createProfile(deviceProfileMapper.convertToDeviceProfileListWS(body,
                                        groupConditionFilterRepository.findAllByGroupConditionId(body.getConditionId()),
                                        getProfileBackup(body.getSendBackup(), body.getSendBackupForNewDevicesOnly(), session, user),
                                        groupId, getProfileVersion(groupId)),
                                (clientType == ClientType.mc ? "MC/" + user.getUsername() : user.getUsername()));
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
            Integer deviceProfileId = deviceProfileRepository.getLastProfileByGroupId(groupId, PageRequest.of(0, 1)).getContent().get(0).getId();
            if (body.getConditionId() != null) {
                DeviceProfileFilterEntity filterEntity = new DeviceProfileFilterEntity();
                filterEntity.setProfileId(deviceProfileId);
                filterEntity.setFilterId(body.getConditionId());
                deviceProfileFilterRepository.save(filterEntity);
            }
            return getProfileDetails(token, deviceProfileId);
        } else {
            try {
                AcsProvider.getAcsWebService(clientType)
                        .updateProfile(deviceProfileMapper.convertToProfileWithIdListWS(body,
                                        groupConditionFilterRepository.findAllByGroupConditionId(body.getConditionId()),
                                        getProfileBackup(body.getSendBackup(), body.getSendBackupForNewDevicesOnly(), session, user), groupId),
                                (clientType == ClientType.mc ? "MC/" + user.getUsername() : user.getUsername()));
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
            if (body.getConditionId() != null) {
                DeviceProfileEntity deviceProfile = deviceProfileRepository.findById(body.getId())
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(DEVICE_PROFILE_NOT_FOUND, body.getId()));
                DeviceProfileFilterEntity filterEntity = deviceProfileFilterRepository.findByProfileId(deviceProfile.getId());
                if (filterEntity != null) {
                    filterEntity.setFilterId(body.getConditionId());
                    deviceProfileFilterRepository.save(filterEntity);
                } else {
                    DeviceProfileFilterEntity clearFilterEntity = new DeviceProfileFilterEntity();
                    clearFilterEntity.setProfileId(deviceProfile.getId());
                    clearFilterEntity.setFilterId(body.getConditionId());
                    deviceProfileFilterRepository.save(clearFilterEntity);
                }
            }
            return getProfileDetails(token, body.getId());
        }
    }

    private ProfileBackup getProfileBackup(final Boolean sendBackup, final Boolean sendBackupForExisting, final Session session, final UserResponse user) {
        if (Boolean.TRUE.equals(sendBackup)) {
            ProfileBackup profileBackup = new ProfileBackup();
            final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                    .stream()
                    .filter(s -> s.getKey().equals("UploadHttp"))
                    .findAny()
                    .orElse(null);
            String location = fileServerService.getDomainFolder(user.getDomainId(), session.getClientType());
            assert serverDetails != null;
            String backupUrl = serverDetails.getAddress() + location;
            profileBackup.setFileTypeId(4);
            profileBackup.setSendBackupForExisting(sendBackupForExisting);
            profileBackup.setUsername(serverDetails.getUsername());
            profileBackup.setPassword(serverDetails.getPassword());
            profileBackup.setUrl(backupUrl);
            return profileBackup;
        }
        return null;
    }

    private String getProfileVersion(final Long groupId) {
        final String defaultVersion = "1.0.0"; // start version from this value
        DeviceProfileEntity profileEntity = deviceProfileRepository.getLastProfileByGroupId(groupId, PageRequest.of(0, 1)).getContent().get(0);
        if (profileEntity == null) {
            return defaultVersion;
        } else {
            return incrementVersion(profileEntity.getVersion());
        }
    }

    private String incrementVersion(String currentVersion) {
        String[] parts = currentVersion.split("\\."); // split version for peaces
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        if (patch < 10) {
            patch++;
            if (patch == 10) {
                patch = 0;
                minor++;
                if (minor == 10) {
                    minor = 0;
                    major++;
                }
            }
        }

        return major + "." + minor + "." + patch;
    }

    public DeviceTabsResponse getProfileTabs(String token, Integer profileId) {
        jwtService.getSession(token);
        DeviceProfileEntity profile = deviceProfileRepository.findById(profileId).orElseThrow(
                () -> new FriendlyEntityNotFoundException(DEVICE_PROFILE_NOT_FOUND, profileId));

        return tabService.getProfileTabs(token, profile.getProductClassGroup().getManufacturerName(), profile.getProductClassGroup().getModel());
    }

    public DeviceObjectsSimpleResponse getProfileTabView(String token, ProfileTabViewBody body) {
        jwtService.getSession(token);
        DeviceProfileEntity profile = deviceProfileRepository.findById(body.getProfileId()).orElseThrow(
                () -> new FriendlyEntityNotFoundException(DEVICE_PROFILE_NOT_FOUND, body.getProfileId()));

        return tabService.getManufAndModelTabView(token, profile.getProductClassGroup().getManufacturerName(),
                profile.getProductClassGroup().getModel(), body.getTabPath());
    }

    public DeviceObjectsResponse getProfileTemplateParameters(String token, ProfileTemplateParametersBody request) {
        jwtService.getSession(token);
        DeviceProfileEntity profile = deviceProfileRepository.findById(request.getProfileId()).orElseThrow(
                () -> new FriendlyEntityNotFoundException(DEVICE_PROFILE_NOT_FOUND, request.getProfileId()));

        return new DeviceObjectsResponse(templateService.getGroupParameters(profile.getProductClassGroup(), request.getFullName()));
    }
}