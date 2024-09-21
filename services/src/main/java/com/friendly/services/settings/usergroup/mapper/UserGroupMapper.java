package com.friendly.services.settings.usergroup.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.user.Permission;
import com.friendly.commons.models.user.PermissionState;
import com.friendly.commons.models.user.UserGroupRequest;
import com.friendly.commons.models.user.UserGroupResponse;
import com.friendly.commons.models.user.UserGroupSimple;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionStateEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionStateType;
import com.friendly.services.settings.usergroup.orm.iotw.model.UserGroupEntity;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserGroupMapper {

    public List<UserGroupResponse> groupEntitiesToGroups(final List<UserGroupEntity> groupEntity,
                                                         final String zoneId,
                                                         final String dateFormat,
                                                         final String timeFormat) {
        if (groupEntity == null) {
            return Collections.EMPTY_LIST;
        }

        return groupEntity.stream()
                          .map(g -> groupEntityToGroup(g, zoneId, dateFormat, timeFormat))
                          .collect(Collectors.toList());
    }

    public UserGroupResponse groupEntityToGroup(final UserGroupEntity groupEntity, final String zoneId,
                                                final String dateFormat, final String timeFormat) {
        final UserGroupResponse.UserGroupResponseBuilder builder =
                UserGroupResponse.builder()
                                 .id(groupEntity.getId())
                                 .createdIso(groupEntity.getCreated())
                                 .updatedIso(groupEntity.getUpdated())
                                 .created(DateTimeUtils.format(groupEntity.getCreated(), zoneId,
                                         dateFormat, timeFormat))
                                 .updated(DateTimeUtils.format(groupEntity.getUpdated(), zoneId,
                                                               dateFormat, timeFormat))
                                 .updater(groupEntity.getUpdater())
                                 .templateVersion(groupEntity.getTemplateVersion());
        if (ObjectUtils.notEqual(groupEntity.getId(), -1L)) {
            builder.name(groupEntity.getName());
        }

        return builder.build();
    }

    public UserGroupResponse groupEntityToGroup(final UserGroupEntity groupEntity) {
        return UserGroupResponse.builder()
                                .id(groupEntity.getId())
                                .name(groupEntity.getName())
                                .createdIso(groupEntity.getCreated())
                                .updatedIso(groupEntity.getUpdated())
                                .updater(groupEntity.getUpdater())
                                .templateVersion(groupEntity.getTemplateVersion())
                                .build();
    }

    public UserGroupEntity groupToGroupEntity(final UserGroupRequest group, final ClientType clientType,
                                              String username) {
        return UserGroupEntity.builder()
                .name(group.getName())
                .clientType(clientType)
                .updater(username)
                .updated(Instant.now())
                .build();
    }

    public PermissionEntity permissionToPermissionEntity(final Long parentId,
                                                         final ClientType clientType,
                                                         final Permission permission) {
        return PermissionEntity.builder()
                               .id(permission.getId())
                               .parentId(parentId)
                               .clientType(clientType)
                               .name(permission.getName())
                               .path(permission.getPath())
                               .iconPath(permission.getIconPath())
                               .location(permission.getLocation())
                               .type(permission.getType())
                               .build();
    }

    public List<Permission> permissionEntitiesToPermissions(final Collection<PermissionEntity> permissionEntities) {
        if (permissionEntities == null || permissionEntities.isEmpty()) {
            return null;
        }

        return permissionEntities.stream()
                                 .map(this::permissionEntityToPermission)
/*                                 .sorted(Comparator.nullsLast(Comparator.comparing(
                                         Permission::getIndex, Comparator.nullsLast(Comparator.naturalOrder()))))*/
                                 .collect(Collectors.toList());
    }

    private Permission permissionEntityToPermission(final PermissionEntity permissionEntity) {
        return Permission.builder()
                         .id(permissionEntity.getId())
                         .name(permissionEntity.getName())
                         .path(permissionEntity.getPath())
                         .iconPath(permissionEntity.getIconPath())
                         .location(permissionEntity.getLocation())
                         .type(permissionEntity.getType())
                         .index(permissionEntity.getIndex())
                         .build();
    }

    public PermissionStateEntity permissionStateToPermissionStateEntity(final Long groupId,
                                                                        final Long permissionId,
                                                                        final PermissionState state,
                                                                        final PermissionStateType type) {
        if (state == null) {
            return null;
        }

        return PermissionStateEntity.builder()
                                    .groupId(groupId)
                                    .permissionId(permissionId)
                                    .type(type)
                                    .checked(state.getChecked())
                                    .visible(state.getVisible())
                                    .build();
    }

    public PermissionState permissionStateEntityToPermissionState(final PermissionStateEntity stateEntity) {
        if (stateEntity == null) {
            return null;
        }

        return PermissionState.builder()
                              .checked(stateEntity.isChecked())
                              .visible(stateEntity.isVisible())
                              .build();
    }

    public List<UserGroupSimple> simpleGroupEntitiesToGroups(final List<Object[]> groupEntity) {
        if (groupEntity == null) {
            return Collections.EMPTY_LIST;
        }

        return groupEntity.stream()
                          .map(this::simpleGroupEntityToGroup)
                          .collect(Collectors.toList());
    }

    private UserGroupSimple simpleGroupEntityToGroup(final Object[] groupEntity) {
        return UserGroupSimple.builder()
                              .id((Long) groupEntity[0])
                              .name((String) groupEntity[1])
                              .build();
    }

}
