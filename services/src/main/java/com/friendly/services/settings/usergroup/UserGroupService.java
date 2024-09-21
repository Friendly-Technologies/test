package com.friendly.services.settings.usergroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.exceptions.FriendlyPermissionException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.settings.UserGroupBody;
import com.friendly.commons.models.settings.UserGroupsBody;
import com.friendly.commons.models.settings.request.CheckDependencyRequest;
import com.friendly.commons.models.settings.request.CheckUserGroupRequest;
import com.friendly.commons.models.settings.response.CheckDependencyResponse;
import com.friendly.commons.models.settings.response.CheckUserGroupResponse;
import com.friendly.commons.models.settings.response.UserGroupsSimpleResponse;
import com.friendly.commons.models.user.Permission;
import com.friendly.commons.models.user.PermissionRequest;
import com.friendly.commons.models.user.PermissionState;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserGroupActType;
import com.friendly.commons.models.user.UserGroupRequest;
import com.friendly.commons.models.user.UserGroupResponse;
import com.friendly.commons.models.user.UserGroupSimple;
import com.friendly.commons.models.user.UserGroupTemplate;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionPK;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionStateEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionStateType;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.UserGroupEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.settings.usergroup.orm.iotw.repository.PermissionRepository;
import com.friendly.services.settings.usergroup.orm.iotw.repository.PermissionStateRepository;
import com.friendly.services.settings.usergroup.orm.iotw.repository.UserGroupRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.settings.acs.AcsUserService;
import com.friendly.services.settings.usergroup.mapper.UserGroupMapper;
import com.friendly.services.settings.usergroup.model.UserGroupTimestamp;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.UserActivityType.ADD_USER_GROUP;
import static com.friendly.commons.models.reports.UserActivityType.DELETE_USER_GROUP;
import static com.friendly.commons.models.reports.UserActivityType.EDIT_USER_GROUP;
import static com.friendly.commons.models.settings.DomainDependency.ACS_USER;
import static com.friendly.commons.models.settings.DomainDependency.DEVICE;
import static com.friendly.commons.models.settings.DomainDependency.USER;
import static com.friendly.commons.models.websocket.ActionType.CREATE;
import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.commons.models.websocket.SettingType.USER_GROUP;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ADMIN_GROUP_CAN_NOT_DELETED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.NO_PERMISSION;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PERMISSION_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USER_GROUP_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USER_GROUP_NOT_UNIQUE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USER_IS_NOT_FOUND;
import static com.friendly.services.settings.usergroup.orm.iotw.model.PermissionStateType.EXECUTE;
import static com.friendly.services.settings.usergroup.orm.iotw.model.PermissionStateType.VIEW;

/**
 * Service that exposes the base functionality for interacting with {@link UserGroupResponse} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupService {
    private final CpeRepository cpeRepository;

    private static final String TEMPLATE_GROUP_NAME = "TEMPLATE";

    @Value("${server.path}")
    private String appPath;

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final UserGroupRepository userGroupRepository;

    @NonNull
    private final PermissionRepository permissionRepository;

    @NonNull
    private final PermissionStateRepository permissionStateRepository;

    @NonNull
    private final UserGroupMapper userGroupMapper;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final StatisticService statisticService;

    @NonNull
    private final WsSender wsSender;

    /**
     * Get User Groups
     *
     * @return user groups entities
     */
    public FTPage<UserGroupResponse> getUserGroups(final String token, UserGroupsBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "updated");
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserEntity user = getUser(session.getUserId());

        final List<Page<UserGroupEntity>> userGroupsList =
                pageable.stream()
                        .map(p -> user.getUsername().equals("admin")
                                ? userGroupRepository.findAllByClientType(clientType, p)
                                : userGroupRepository.findNotAdminGroupsByClientType(clientType, p))
                        .collect(Collectors.toList());

        final FTPage<UserGroupResponse> userGroupPage = new FTPage<>();
        final List<UserGroupResponse> userGroups =
                userGroupsList.stream()
                        .map(Page::getContent)
                        .flatMap(ug -> userGroupMapper.groupEntitiesToGroups(ug,
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList());

        return buildUserGroupPage(userGroupsList, userGroupPage, userGroups);
    }

    public UserGroupsSimpleResponse getSimpleUserGroups(final String token) {
        final ClientType clientType = jwtService.getClientTypeByHeaderAuth(token);

        List<UserGroupSimple> userGroups = userGroupMapper.simpleGroupEntitiesToGroups(
                userGroupRepository.getSimpleUserGroups(clientType));
        return new UserGroupsSimpleResponse(userGroups);
    }

    private FTPage<UserGroupResponse> buildUserGroupPage(
            final List<Page<UserGroupEntity>> userGroupsList,
            final FTPage<UserGroupResponse> userGroupPage,
            final List<UserGroupResponse> userGroups) {
        return userGroupPage.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(userGroupsList))
                .items(userGroups)
                .build();
    }

    /**
     * Get User Groups by UserId
     *
     * @return user group entity
     */
    public UserGroupResponse getUserGroup(final String token, final UserGroupBody body) {
        final Session session = jwtService.getSession(token);
        final Long userIdFromToken = session.getUserId();
        final ClientType clientType = session.getClientType();
        final String zoneId = session.getZoneId();
        final UserEntity user = getUser(userIdFromToken);
        final Long groupId = body.getGroupId();
        final Long userId = body.getUserId();

        if (Objects.nonNull(groupId)) {
            final UserGroupResponse userGroup =
                    userGroupRepository.findById(groupId)
                            .map(g -> userGroupMapper.groupEntityToGroup(g, zoneId, user.getDateFormat(),
                                    user.getTimeFormat()))
                            .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND));
            return buildUserGroup(clientType, zoneId, user.getDateFormat(), user.getTimeFormat(), userGroup);
        }

        return Objects.nonNull(userId)
                ? getUserGroup(userId, clientType, zoneId, user.getDateFormat(), user.getTimeFormat())
                : getUserGroup(userIdFromToken, clientType, zoneId, user.getDateFormat(), user.getTimeFormat());
    }

    /**
     * Update user group template
     *
     * @return user group entity
     */

    public boolean updateUserGroupTemplate(final ClientType clientType,
                                           final Long templateId,
                                           final UserGroupTemplate userGroup,
                                           final List<SavedPermission> savedPermissions) {

        final UserGroupEntity userGroupTemplateEntity =
                getUserGroupTemplateEntity(clientType).toBuilder()
                        .id(templateId)
                        .templateVersion(userGroup.getTemplateVersion())
                        .build();
        final UserGroupResponse userGroupTemplate =
                Optional.of(userGroupRepository.saveAndFlush(userGroupTemplateEntity))
                        .map(userGroupMapper::groupEntityToGroup)
                        .orElse(null);

        if (!userGroupRepository.findById(-1L).isPresent()) {
            userGroupRepository.updateIdForTemplateSC();
        }
        if (!userGroupRepository.findById(-2L).isPresent()) {
            userGroupRepository.updateIdForTemplateMC();
        }

        //List<SavedPermission> savedPermissions = deleteAllPermissions();
        final List<PermissionEntity> permissions = new ArrayList<>();
        userGroup.getPermissions()
                .forEach(p -> savePermission(templateId, null, clientType, p, permissions));

        deletePermissions(clientType, permissions);
        setPermissionStatesToAllGroups(clientType, permissions, savedPermissions);

        wsSender.sendSettingEvent(clientType, UPDATE, USER_GROUP, userGroupTemplate);
        return true;
    }

    public List<SavedPermission> deleteAllPermissions() {
        List<SavedPermission> permissions = permissionStateRepository
                .findAll()
                .stream()
                .map(p -> new SavedPermission(p.getGroupId(),
                        permissionRepository.findById(p.getPermissionId()).orElse(getPermission()).getName(),
                        p.getType(),
                        p.isChecked(),
                        p.isVisible()))
                .collect(Collectors.toList());
        permissionStateRepository.deleteAll();
        permissionRepository.deleteAll();

        return permissions;
    }

    private PermissionEntity getPermission() {
        return new PermissionEntity().toBuilder()
                .name(null)
                .build();
    }

    public void addCustomFramePermission(final ClientType clientType, final String name, final boolean hasSend) {
        final PermissionEntity mainPermission =
                permissionRepository.findAllByClientTypeAndPathAndType(clientType, "main", "tab")
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(PERMISSION_NOT_FOUND,
                                "main"));

        final String parentName = StringUtils.substringBefore(mainPermission.getName(), "main");
        final Permission permission = Permission.builder()
                .name(parentName + name)
                .type("frame-custom")
                .path(name)
                .build();
        final long templateId = clientType == ClientType.sc ? -1L : -2L;
        final PermissionState state = PermissionState.builder()
                .checked(true)
                .visible(true)
                .build();
        final List<PermissionStateEntity> states = new ArrayList<>();
        states.add(savePermissionState(templateId, permission.getId(), state, EXECUTE));
        states.add(savePermissionState(templateId, permission.getId(), state, VIEW));
        if (hasSend) {
            final PermissionEntity sendButton = createNewPermission(permission.getId(), clientType,
                    Permission.builder()
                            .name(parentName + "send")
                            .type("button-action")
                            .build());
            states.add(savePermissionState(templateId, sendButton.getId(), state, EXECUTE));
            states.add(savePermissionState(templateId, sendButton.getId(), state, VIEW));
        }
        userGroupRepository.findAllByClientType(clientType)
                .stream()
                .map(UserGroupEntity::getId)
                .forEach(groupId -> states.forEach(s -> setPermissionStateIfNull(groupId, s, null)));
        final List<PermissionEntity> permissions = new ArrayList<>();
        savePermission(clientType == ClientType.sc ? -1L : -2L, mainPermission.getId(), clientType,
                permission, permissions);
        setPermissionStatesToAllGroups(clientType, permissions, null);
    }

    public void updateCustomFramePermission(final ClientType clientType, final String name,
                                            final String newName, final boolean hasSend) {
        final PermissionEntity customPermission =
                permissionRepository.findAllByClientTypeAndPathAndType(clientType, name, "frame-custom")
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(PERMISSION_NOT_FOUND,
                                "frame-custom"));
        final String parentName = StringUtils.substringBefore(customPermission.getName(), customPermission.getPath());
        if (ObjectUtils.notEqual(name, newName)) {
            customPermission.setName(parentName + newName);
            customPermission.setPath(newName);
            permissionRepository.saveAndFlush(customPermission);
        }

        final List<PermissionEntity> sendButtons =
                permissionRepository.findAllByClientTypeAndParentId(clientType, customPermission.getId());
        if (sendButtons.isEmpty() && hasSend) {
            final long templateId = clientType == ClientType.sc ? -1L : -2L;
            final PermissionState state = PermissionState.builder()
                    .checked(true)
                    .visible(true)
                    .build();
            final PermissionEntity sendButton = createNewPermission(customPermission.getId(), clientType,
                    Permission.builder()
                            .name(parentName + "send")
                            .type("button-action")
                            .build());
            final PermissionStateEntity executeState = savePermissionState(templateId, sendButton.getId(),
                    state, EXECUTE);
            final PermissionStateEntity viewState = savePermissionState(templateId, sendButton.getId(),
                    state, VIEW);
            userGroupRepository.findAllByClientType(clientType)
                    .stream()
                    .map(UserGroupEntity::getId)
                    .forEach(groupId -> {
                        setPermissionStateIfNull(groupId, executeState, null);
                        setPermissionStateIfNull(groupId, viewState, null);
                    });
        } else if (!sendButtons.isEmpty() && !hasSend) {
            final List<Long> ids = sendButtons.stream()
                    .map(PermissionEntity::getId)
                    .collect(Collectors.toList());
            permissionRepository.deletePermissions(clientType, ids);
        }
    }

    public void deleteCustomFramePermission(final ClientType clientType, final List<String> name) {
        final List<Long> permissionIds =
                permissionRepository.findAllByClientTypeAndPathInAndType(clientType, name, "frame-custom")
                        .stream()
                        .map(PermissionEntity::getId)
                        .collect(Collectors.toList());

        permissionRepository.deletePermissions(clientType, permissionIds);
        permissionRepository.deletePermissionsByParentId(clientType, permissionIds);
    }

    private void setPermissionStatesToAllGroups(final ClientType clientType,
                                                final List<PermissionEntity> permissions,
                                                final List<SavedPermission> savedPermissions) {
        userGroupRepository.findAllByClientType(clientType)
                .stream()
                .map(UserGroupEntity::getId)
                .forEach(groupId -> permissions.forEach(p -> setPermissionStatesIfNull(groupId, p, savedPermissions)));
    }

    private void setPermissionStatesIfNull(final Long groupId, final PermissionEntity permissionEntity, final List<SavedPermission> savedPermissions) {
        setPermissionStateIfNull(groupId, permissionEntity.getExecuteState(), savedPermissions);
        setPermissionStateVisibleIfNull(groupId, permissionEntity.getViewState(), savedPermissions);
    }

    private void setPermissionStateIfNull(final Long groupId, final PermissionStateEntity state, List<SavedPermission> savedPermissions) {
        if (permissionStateRepository.findById(PermissionPK.builder()
                        .groupId(groupId)
                        .permissionId(state.getPermissionId())
                        .type(state.getType())
                        .build())
                .isPresent()) {
            return;
        }

        if (savedPermissions != null) {

            SavedPermission perm = getByGroupId(groupId, savedPermissions);

            if (perm != null) {
                state.setVisible(state.isVisible());
                state.setChecked(perm.isChecked());
            }
        }

        permissionStateRepository.saveAndFlush(PermissionStateEntity.builder()
                .groupId(groupId)
                .permissionId(state.getPermissionId())
                .type(state.getType())
                .checked(state.isChecked())
                .visible(state.isVisible())
                .build());
    }

    private void setPermissionStateVisibleIfNull(final Long groupId, final PermissionStateEntity state, List<SavedPermission> savedPermissions) {
        PermissionPK pk = PermissionPK.builder()
                .groupId(groupId)
                .permissionId(state.getPermissionId())
                .type(state.getType())
                .build();
        Optional<PermissionStateEntity> currentState = permissionStateRepository.findById(pk);
        if (currentState.isPresent()) {
            currentState.get().setVisible(state.isVisible());
            return;
        }

        if (savedPermissions != null) {

            SavedPermission perm = getByGroupId(groupId, savedPermissions);

            if (perm != null) {
                state.setVisible(state.isVisible());
                state.setChecked(perm.isChecked());
            }

        }
        permissionStateRepository.saveAndFlush(PermissionStateEntity.builder()
                .groupId(groupId)
                .permissionId(state.getPermissionId())
                .type(state.getType())
                .checked(state.isChecked())
                .visible(state.isVisible())
                .build());
    }


    private void deletePermissions(final ClientType clientType, final List<PermissionEntity> permissions) {
        final List<Long> frameCustomIds = permissionRepository.findFrameCustomIds(clientType);
        final List<Long> exceptIds = permissions.stream()
                .map(PermissionEntity::getId)

                .collect(Collectors.toList());
        exceptIds.addAll(frameCustomIds);
        if (frameCustomIds.isEmpty()) {
            frameCustomIds.add(0L);
        }
        if (exceptIds.isEmpty()) {
            exceptIds.add(0L);
        }
        final List<Long> permissionIds =
                permissionRepository.findIdsByClientTypeAndIdNotIn(clientType, exceptIds, frameCustomIds);
        if (!permissionIds.isEmpty()) {
            permissionRepository.deletePermissions(clientType, permissionIds);
            permissionStateRepository.deletePermissionStates(permissionIds);
        }
    }

    private UserGroupEntity getUserGroupTemplateEntity(final ClientType clientType) {

        return userGroupRepository.findUserGroupEntityByClientTypeAndId(clientType, clientType.templateId)
                .orElse(UserGroupEntity.builder()
                        .id(clientType.templateId)
                        .name(TEMPLATE_GROUP_NAME)
                        .clientType(clientType)
                        .build());
    }

    private void savePermission(final Long groupId, final Long parentId, final ClientType clientType,
                                final Permission permission, final List<PermissionEntity> permissions) {
        final Optional<PermissionEntity> optionalPermissionEntity =
                permissionRepository.findByClientTypeAndNameAndParentId(clientType, permission.getName(), parentId)
                        .map(p -> permissionRepository.saveAndFlush(p.toBuilder()
                                .type(permission.getType())
                                .iconPath(permission.getIconPath())
                                .location(permission.getLocation())
                                .path(permission.getPath())
                                .build()));
        final PermissionEntity permissionEntity =
                optionalPermissionEntity.orElseGet(() -> createNewPermission(parentId, clientType, permission));

        permissionEntity.setExecuteState(
                savePermissionState(groupId, permissionEntity.getId(), permission.getExecuteState(), EXECUTE));
        permissionEntity.setViewState(
                savePermissionState(groupId, permissionEntity.getId(), permission.getViewState(), VIEW));
        permissions.add(permissionEntity);

        if (permission.getPermissions() != null) {
            permission.getPermissions()
                    .forEach(p -> savePermission(groupId, permissionEntity.getId(), clientType, p, permissions));
        }
    }

    private PermissionEntity createNewPermission(final Long parentId, final ClientType clientType,
                                                 final Permission permission) {
        return permissionRepository.saveAndFlush(
                userGroupMapper.permissionToPermissionEntity(parentId, clientType, permission));
    }

    private PermissionStateEntity savePermissionState(final Long groupId,
                                                      final Long permissionId,
                                                      final PermissionState state,
                                                      final PermissionStateType type) {
        return permissionStateRepository.save(
                userGroupMapper.permissionStateToPermissionStateEntity(groupId, permissionId, state, type));
    }

    /**
     * Crete User Group / Update Permissions States
     */
    @Transactional
    public UserGroupResponse createGroupOrUpdatePermissionStates(final String token, final UserGroupRequest userGroup) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserEntity user = getUser(session.getUserId());
        final UserGroupActType actionType = userGroup.getActionType();
        final boolean isNew = !actionType.equals(UserGroupActType.UPDATE);

        if (actionType.equals(UserGroupActType.UPDATE) && (userGroup.getId() == null || userGroup.getId().equals(-1L) || userGroup.getId().equals(-2L))) {
            throw new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND);
        }
        if (isNew && userGroupRepository.existsByNameAndClientType(userGroup.getName(), clientType)) {
            throw new FriendlyIllegalArgumentException(USER_GROUP_NOT_UNIQUE, userGroup.getName());
        }

        final PermissionEntity userGroupPermission = getUserGroupPermission(clientType, user.getUserGroupId());
        UserGroupEntity userGroupEntity;
        if (isNew) {
            validatePermission(clientType, user.getUserGroupId(), userGroupPermission, ".add", "add user group");
            userGroupEntity = userGroupRepository.saveAndFlush(
                    userGroupMapper.groupToGroupEntity(userGroup, clientType, user.getName()));
        } else {
            validatePermission(clientType, user.getUserGroupId(), userGroupPermission, ".save", "update permissions");
            userGroupEntity = userGroupRepository.findUserGroupEntityByClientTypeAndId(clientType, userGroup.getId())
                    .orElseThrow(() -> new FriendlyEntityNotFoundException(
                            USER_GROUP_NOT_FOUND));
            userGroupEntity.setUpdated(Instant.now());
            userGroupEntity.setName(userGroup.getName());
            userGroupEntity.setClientType(clientType);
            userGroupEntity.setUpdater(user.getName());
            userGroupRepository.saveAndFlush(userGroupEntity);
        }
        final Long groupId = userGroupEntity.getId();
        final Long duplicateGroupId = actionType.equals(UserGroupActType.DUPLICATE)
                ? userGroup.getId()
                : clientType.templateId;

        final Set<PermissionStateEntity> newPermissionStates =
                getNewPermissionStates(groupId, duplicateGroupId, userGroup.getPermissions());

        if (isNew) {
            final Map<String, PermissionStateEntity> oldPermissionStatesMap =
                    permissionStateRepository.getPermissionStates(duplicateGroupId)
                            .stream()
                            .collect(Collectors.toMap(ps -> ps.getPermissionId().toString()
                                            + ps.getType(),
                                    ps -> ps.toBuilder()
                                            .groupId(groupId)
                                            .build(),
                                    (u, v) -> {
                                        throw new IllegalStateException(
                                                String.format("Duplicate key %s",
                                                        u));
                                    }, LinkedHashMap::new));

            newPermissionStates.forEach(ps -> oldPermissionStatesMap.put(ps.getPermissionId().toString()
                    + ps.getType(), ps));
            permissionStateRepository.saveAll(oldPermissionStatesMap.values());
        } else {
            permissionStateRepository.saveAll(newPermissionStates);
        }
        permissionStateRepository.flush();

        final UserGroupResponse createdUserGroup =
                userGroupRepository.findById(groupId)
                        .map(g -> userGroupMapper.groupEntityToGroup(g, session.getZoneId(),
                                user.getDateFormat(),
                                user.getTimeFormat()))
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND));
        createdUserGroup.setPermissions(getPermissions(clientType, groupId));

        if (isNew) {
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(ADD_USER_GROUP)
                    .note(createdUserGroup.getName())
                    .build());
            wsSender.sendSettingEvent(clientType, CREATE, USER_GROUP, createdUserGroup);
        } else {
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(EDIT_USER_GROUP)
                    .note(createdUserGroup.getName())
                    .build());
            wsSender.sendSettingEvent(clientType, UPDATE, USER_GROUP, createdUserGroup);
        }
        return createdUserGroup;
    }

    private PermissionEntity getUserGroupPermission(final ClientType clientType, final Long userGroupId) {
        final PermissionEntity userGroupPermission =
                permissionRepository.findAllByClientTypeAndPath(clientType, "user-group")
                        .stream()
                        .findFirst()
                        .orElse(null);
        if (userGroupPermission != null) {
            final Boolean userGroupExecuteState =
                    permissionStateRepository.findById(PermissionPK.builder()
                                    .permissionId(userGroupPermission.getId())
                                    .groupId(userGroupId)
                                    .type(EXECUTE)
                                    .build())
                            .map(PermissionStateEntity::isChecked)
                            .orElse(true);
            if (!userGroupExecuteState) {
                throw new FriendlyPermissionException(NO_PERMISSION, "user group");
            }
        }
        return userGroupPermission;
    }

    private void validatePermission(final ClientType clientType, final Long userGroupId,
                                    final PermissionEntity userGroupPermission, final String endsWithFilter,
                                    final String errorMessage) {
        if (userGroupPermission != null) {
            final Boolean addPermission =
                    permissionRepository.findAllByClientTypeAndParentId(clientType, userGroupPermission.getId())
                            .stream()
                            .filter(p -> p.getName().endsWith(endsWithFilter))
                            .findAny()
                            .map(PermissionEntity::getId)
                            .flatMap(id -> permissionStateRepository.findById(
                                            PermissionPK.builder()
                                                    .permissionId(id)
                                                    .groupId(userGroupId)
                                                    .type(EXECUTE)
                                                    .build())
                                    .map(PermissionStateEntity::isChecked))
                            .orElse(true);
            if (!addPermission) {
                throw new FriendlyPermissionException(NO_PERMISSION, errorMessage);
            }
        }
    }

    private Set<PermissionStateEntity> getNewPermissionStates(final Long groupId,
                                                              final Long duplicateGroupId,
                                                              final Map<Long, PermissionRequest> permissions) {
        final Set<PermissionStateEntity> permissionStates = new HashSet<>();
        if (permissions == null) return permissionStates;
        permissions.keySet().forEach(key -> {
            if (key == null) return;
            addPermissionState(groupId, duplicateGroupId, permissionStates, key,
                    permissions.get(key).getExecuteState(), EXECUTE);
            addPermissionState(groupId, duplicateGroupId, permissionStates, key,
                    permissions.get(key).getViewState(), VIEW);
        });
        return permissionStates;
    }

    private void addPermissionState(final Long groupId,
                                    final Long duplicateGroupId,
                                    final Set<PermissionStateEntity> permissionStates,
                                    final Long permissionId,
                                    final PermissionState state,
                                    final PermissionStateType stateType) {
        if (state != null && state.getChecked() != null) {
            final Optional<PermissionStateEntity> oldPermission =
                    permissionStateRepository.findById(PermissionPK.builder()
                            .permissionId(permissionId)
                            .groupId(duplicateGroupId)
                            .type(stateType)
                            .build());
            if (oldPermission.isPresent()) {
                final PermissionStateEntity permissionState =
                        oldPermission.get()
                                .toBuilder()
                                .groupId(groupId)
                                .checked(state.getChecked())
                                .build();

                permissionStates.add(permissionState);
            }
        }
    }

    /**
     * Delete user groups
     */
    @Transactional
    public void deleteUserGroups(final String token, final List<Long> userGroupIds) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userGroupId = userRepository.getUserGroup(session.getUserId())
                .map(UserGroupEntity::getId)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(
                        USER_GROUP_NOT_FOUND));

        final PermissionEntity userGroupPermission = getUserGroupPermission(clientType, userGroupId);
        validatePermission(clientType, userGroupId, userGroupPermission, ".delete", "delete user group");

        userGroupIds.stream()
                .filter(this::isNotTemplateAndAdminGroup)
                .forEach(id -> {
                    final Optional<UserGroupEntity> userGroup = userGroupRepository.findById(id);
                    if (userGroup.isPresent()) {
                        if (userRepository.existsForUserGroup(userGroup.get().getId()).isPresent()) {
                            throw new FriendlyPermissionException(NO_PERMISSION, "User group: " + userGroup.get().getName() + " contains users");
                        }
                        userGroupRepository.deleteById(id);
                        userRepository.deleteUsersByUserGroup(id);
                        permissionStateRepository.deleteAllByGroupId(id);
                        statisticService.addUserLogAct(UserActivityLog.builder()
                                .userId(session.getUserId())
                                .clientType(clientType)
                                .activityType(
                                        DELETE_USER_GROUP)
                                .note(userGroup.get().getName())
                                .build());
                        wsSender.sendSettingEvent(clientType, UPDATE, USER_GROUP, id);
                    }
                });
    }

    private UserGroupResponse getUserGroup(final Long userId, final ClientType clientType, final String zoneId,
                                           final String dateFormat, final String timeFormat) {
        final UserGroupResponse userGroup =
                userRepository.getUserGroup(userId)
                        .map(g -> userGroupMapper.groupEntityToGroup(g, zoneId, dateFormat, timeFormat))
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND));
        return buildUserGroup(clientType, zoneId, dateFormat, timeFormat, userGroup);
    }

    private UserGroupResponse buildUserGroup(final ClientType clientType,
                                             final String zoneId,
                                             final String dateFormat,
                                             final String timeFormat,
                                             final UserGroupResponse userGroup) {
        final UserGroupEntity userGroupTemplate =
                userGroupRepository.findUserGroupEntityByClientTypeAndId(clientType,
                                clientType.templateId)
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND));
        return userGroup.toBuilder()
                .templateVersion(userGroupTemplate.getTemplateVersion())
                .updatedIso(userGroupTemplate.getUpdated())
                .updated(DateTimeUtils
                        .format(userGroupTemplate.getUpdated(), zoneId, dateFormat, timeFormat))
                .updater(userGroupTemplate.getUpdater())
                .permissions(getPermissions(clientType, userGroup.getId()))
                .build();
    }

    private List<Permission> getPermissions(final ClientType clientType, final Long groupId) {
        return getPermissions(clientType, groupId, null);
    }

    private List<Permission> getPermissions(final ClientType clientType, final Long groupId, final Long parentId) {
        final List<Permission> permissions = userGroupMapper.permissionEntitiesToPermissions(
                permissionRepository.findAllByClientTypeAndParentId(clientType, parentId));
        final Map<String, PermissionStateEntity> permissionStates =
                permissionStateRepository.getPermissionStates(groupId)
                        .stream()
                        .collect(Collectors.toMap(ps -> ps.getPermissionId().toString() + ps.getType(),
                                ps -> ps,
                                (u, v) -> {
                                    throw new IllegalStateException(
                                            String.format("Duplicate key %s", u));
                                }, LinkedHashMap::new));
        if (permissions != null) {
            putPermissionStatesEntityToMap(permissionStates, permissions);
            permissions.forEach(p -> p.setPermissions(getPermissions(clientType, groupId, p.getId())));
        }

        return permissions;
    }

    private void putPermissionStatesEntityToMap(final Map<String, PermissionStateEntity> permissionStates,
                                                final List<Permission> permissions) {
        if (permissions == null) return;
        permissions.forEach(permission -> {
            if (permission == null) return;
            permission.setExecuteState(userGroupMapper.permissionStateEntityToPermissionState(
                    permissionStates.get(permission.getId().toString() + EXECUTE)));
            permission.setViewState(userGroupMapper.permissionStateEntityToPermissionState(
                    permissionStates.get(permission.getId().toString() + VIEW)));
            putPermissionStatesEntityToMap(permissionStates, permission.getPermissions());
        });
    }


    private boolean isNotTemplateAndAdminGroup(final Long groupId) {
        final String groupName =
                userGroupRepository.findById(groupId)
                        .map(UserGroupEntity::getName)
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND));
        if (groupName.equals("admin") || groupName.equals(TEMPLATE_GROUP_NAME)) {
            throw new FriendlyIllegalArgumentException(ADMIN_GROUP_CAN_NOT_DELETED);
        }
        return true;
    }

    private UserEntity getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_IS_NOT_FOUND, userId));
    }

    public SavedPermission getByGroupId(Long groupId, List<SavedPermission> savedPermissions) {
        return savedPermissions.stream().filter(perm -> perm.groupId == groupId).findFirst().orElse(null);
    }

    public void updateUserGroupTemplates(ClientType clientType) {

        final UserGroupTemplate userGroupTemplate;
        Path permissionPathUi = ClientType.sc.equals(clientType) ?
                Paths.get(appPath + "../webapps/support-center/assets/app-permissions-config.json") :
                Paths.get(appPath + "../webapps/management-console/assets/app-permissions-config.json");
        if (!Files.exists(permissionPathUi)) {
            log.info("Permission file is absent on path: " + permissionPathUi);
            return;
        }

        Path permissionPathBack = Paths.get(appPath + "app-permissions/" + clientType.name() + "/" + "app-permissions-config.json");

        try (FileReader readerUi = new FileReader(permissionPathUi.toFile());
             FileReader readerBack = Files.exists(permissionPathBack) ? new FileReader(permissionPathBack.toFile()) : null) {

            boolean different = true;
            if (readerBack != null) {
                try {
                    different = !FileUtils.contentEquals(permissionPathUi.toFile(), permissionPathBack.toFile());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            if (different) {
                log.info("Permission file: " + permissionPathUi + " is changed or new => updating it");
                userGroupTemplate = mapper.readValue(readerUi, UserGroupTemplate.class);
                List<UserGroupService.SavedPermission> savedPermissions = deleteAllPermissions();
                updateUserGroupTemplate(clientType, clientType.templateId, userGroupTemplate, savedPermissions);
                FileUtils.copyFile(permissionPathUi.toFile(), permissionPathBack.toFile());
            }

        } catch (IOException e) {
            log.error("Error while processing permissions", e);
        }

    }

    private void updateUserGroupTemplate(ClientType clientType, UserGroupTemplate userGroupTemplate) {
//        final Integer templateVersion =
//                userGroupRepository.findUserGroupEntityByClientTypeAndId(clientType, clientType.templateId)
//                        .map(UserGroupEntity::getTemplateVersion)
//                        .orElse(null);

//        if (templateVersion == null || templateVersion < userGroupTemplate.getTemplateVersion()) {
            List<UserGroupService.SavedPermission> savedPermissions = deleteAllPermissions();
            updateUserGroupTemplate(clientType, clientType.templateId, userGroupTemplate, savedPermissions);
//        }
    }


    @Transactional
    public boolean updatePermissionConfig(final String token, final MultipartFile config) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        File file = AcsUserService.convert(config);

        String fileName = appPath +
                (clientType == ClientType.mc ? "app-permissions/mc/" : "app-permissions/sc/") +
                "app-permissions-config.json";
        try (FileReader reader = new FileReader(file)) {
            final UserGroupTemplate userGroupTemplate = mapper.readValue(reader, UserGroupTemplate.class);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            mapper.writeValue(new File(fileName), userGroupTemplate);
            updateUserGroupTemplate(clientType, userGroupTemplate);
            file.delete();
        } catch (IOException e) {
            // Handle the exception
        }
        return true;
    }

    public CheckUserGroupResponse checkUserGroupExists(String token, CheckUserGroupRequest body) {
        jwtService.getSession(token);
        List<Integer> ids = body.getIds();
        List<Integer> allIds = userRepository.getAllUserGroupsWithUsers();

        CheckUserGroupResponse response = new CheckUserGroupResponse();
        response.setIds(new ArrayList<>());

        ids.stream()
                .filter(allIds::contains)
                .forEach(value -> response.getIds().add(value));

        response.setExist(!response.getIds().isEmpty());

        return response;

    }

    public CheckDependencyResponse checkDependencyExist(String token, CheckDependencyRequest body) {
        jwtService.getSession(token);
        List<Integer> ids = body.getIds();
        List<Integer> usersIds = userRepository.getAllDomainIdsWithUsers();
        List<Integer> acsUsersIds = userRepository.getAllDomainIdsWithAcsUsers();
        List<Integer> devicesIds = cpeRepository.getAllDomainIdsWithDevices();


        CheckDependencyResponse response = new CheckDependencyResponse();
        response.setExist(false);
        response.setDependencies(new ArrayList<>());

        ids.forEach(id -> {
            if (!response.getDependencies().contains(USER) &&
                    usersIds.contains(id)) {
                response.setExist(true);
                response.getDependencies().add(USER);
            }
            if (!response.getDependencies().contains(ACS_USER) &&
                    acsUsersIds.contains(id)) {
                response.setExist(true);
                response.getDependencies().add(ACS_USER);
            }
            if (!response.getDependencies().contains(DEVICE) &&
                    devicesIds.contains(id)) {
                response.setExist(true);
                response.getDependencies().add(DEVICE);
            }
        });


        return response;
    }

    public UserGroupTimestamp getUserGroupTimestamp(String token) {
        final Session session = jwtService.getSession(token);
        final UserEntity user = getUser(session.getUserId());
        final Long groupId = user.getUserGroupId();
        final Instant userGroupUpdated = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND, groupId))
                .getUpdated();
        return UserGroupTimestamp.builder()
                .updatedIso(userGroupUpdated.toString())
                .build();
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class SavedPermission {
        private long groupId;
        private String permissionName;
        private PermissionStateType type;
        private boolean checked;
        private boolean visible;
    }

}
