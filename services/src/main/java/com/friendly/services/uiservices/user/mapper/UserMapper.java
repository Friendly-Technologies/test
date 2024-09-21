package com.friendly.services.uiservices.user.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.user.Locale;
import com.friendly.commons.models.user.UserGroupSimple;
import com.friendly.commons.models.user.UserRequest;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.user.UserStatusType;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.EntityDTOMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.commons.models.user.UserStatusType.ACTIVE;
import static com.friendly.commons.models.user.UserStatusType.BLOCKED;
import static com.friendly.commons.models.user.UserStatusType.EXPIRED;

@Component
public class UserMapper {

    public List<UserResponse> userEntitiesToUsers(final List<UserEntity> userEntities, final String zoneId,
                                                  final String dateFormat, final String timeFormat) {
        return userEntities.stream()
                .map(u -> userEntityToUser(u, zoneId, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    public UserResponse userEntityToUser(final UserEntity userEntity, final String zoneId) {
        return userEntityToUser(userEntity, zoneId, userEntity.getDateFormat(), userEntity.getTimeFormat());
    }

    public UserResponse userEntityToUser(final UserEntity userEntity, final String zoneId,
                                         final String dateFormat, final String timeFormat) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .dateFormat(userEntity.getDateFormat())
                .timeFormat(userEntity.getTimeFormat())
                .blocked(userEntity.getBlocked())
                .isChangePassword(userEntity.getIsChangePassword())
                .status(getStatus(userEntity.getBlocked(), userEntity.getExpireDate()))
                .userGroupId(userEntity.getUserGroupId())
                .localeId(userEntity.getLocaleId())
                .domainId(userEntity.getDomainId())
                .expireDateIso(userEntity.getExpireDate())
                .expireDate(DateTimeUtils.format(userEntity.getExpireDate(), zoneId, dateFormat, timeFormat))
                .clientType(userEntity.getClientType())
                .userGroup(EntityDTOMapper.entityToDto(userEntity.getUserGroup(), UserGroupSimple.class))
                .locale(EntityDTOMapper.entityToDto(userEntity.getLocale(), Locale.class))
                .themeName(userEntity.getThemeName())
                .build();
    }

    public UserEntity userToUserEntity(final UserRequest user, final ClientType clientType) {
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .name(user.getName())
                .email(user.getEmail())
                .dateFormat(user.getDateFormat())
                .timeFormat(user.getTimeFormat())
                .domainId(user.getDomainId() == null || user.getDomainId() == -1 ? null : user.getDomainId())
                .userGroupId(user.getUserGroupId())
                .localeId(user.getLocaleId())
                .blocked(user.getBlocked())
                .isChangePassword(user.getIsChangePassword())
                .expireDate(user.getExpireDateIso())
                .clientType(clientType)
                .themeName(user.getThemeName())
                .build();
    }

    public UserResponse userEntityToUserWithPassword(final UserEntity userEntity) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .dateFormat(userEntity.getDateFormat())
                .timeFormat(userEntity.getTimeFormat())
                .domainId(userEntity.getDomainId())
                .userGroupId(userEntity.getUserGroupId())
                .localeId(userEntity.getLocaleId())
                .lastLogin(userEntity.getLastLogin())
                .blocked(userEntity.getBlocked())
                .isChangePassword(userEntity.getIsChangePassword())
                .failedAttempts(userEntity.getFailedAttempts() == null ? 0 : userEntity.getFailedAttempts())
                .status(getStatus(userEntity.getBlocked(), userEntity.getExpireDate()))
                .expireDateIso(userEntity.getExpireDate())
                .expireDate(null)
                .clientType(userEntity.getClientType())
                .themeName(userEntity.getThemeName())
                .userGroup(EntityDTOMapper.entityToDto(userEntity.getUserGroup(), UserGroupSimple.class))
                .locale(EntityDTOMapper.entityToDto(userEntity.getLocale(), Locale.class))
                .build();
    }

    private UserStatusType getStatus(final Boolean isBlocked, final Instant expireDate) {
        if (isBlocked != null && isBlocked) {
            return BLOCKED;
        }

        if (expireDate != null && expireDate.isBefore(Instant.now())) {
            return EXPIRED;
        }

        return ACTIVE;
    }

}
