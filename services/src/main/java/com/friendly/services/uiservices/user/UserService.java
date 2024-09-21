package com.friendly.services.uiservices.user;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.exceptions.FriendlyUnauthorizedUserException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.FTPageDetails;
import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.auth.request.RestorePasswordRequest;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.user.AllUsersBody;
import com.friendly.commons.models.user.DomainSimple;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserRequest;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.infrastructure.config.MailConfig;
import com.friendly.services.device.info.orm.acs.model.DomainEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.UserGroupEntity;
import com.friendly.services.settings.usergroup.orm.iotw.repository.UserGroupRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.sessions.SessionService;
import com.friendly.services.settings.userinterface.InterfaceService;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.mapper.UserMapper;
import com.friendly.services.infrastructure.utils.LicenseUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.UserActivityType.CREATE_NEW_USER;
import static com.friendly.commons.models.reports.UserActivityType.DELETE_USER;
import static com.friendly.commons.models.reports.UserActivityType.EDIT_USER_SETTING;
import static com.friendly.commons.models.reports.UserActivityType.FORGOT_PASSWORD;
import static com.friendly.commons.models.reports.UserActivityType.PASSWORD_CHANGE;
import static com.friendly.commons.models.websocket.ActionType.CREATE;
import static com.friendly.commons.models.websocket.ActionType.DELETE;
import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CLIENT_TYPES_ARE_NOT_COMPATIBLE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DOMAIN_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.EMAIL_ERROR;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.INTERFACE_VALUE_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.RESTORE_PASSWORD_FAILED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USERNAME_AND_EMAIL_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USER_GROUP_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USER_NOT_UNIQUE;
import static com.friendly.services.settings.userinterface.InterfaceItem.PASSWORD_DAYS_VALID;

/**
 * Service that exposes the base functionality for interacting with {@link UserResponse} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @NonNull
    private final PasswordEncoder passwordEncoder;
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final UserMapper userMapper;
    @NonNull
    private final UserServiceHelper userServiceHelper;
    @NonNull
    private final DomainService domainService;
    @NonNull
    private final StatisticService statisticService;
    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final SessionService sessionService;
    @NonNull
    private final UserGroupRepository userGroupRepository;
    @NonNull
    private final WsSender wsSender;
    @NonNull
    private final InterfaceService interfaceService;
    @NonNull
    private final MailConfig mailConfig;

    public UserResponse getUserByUsernameAndDomain(final String userName, final ClientType clientType,
                                                   final String rootDomainName) {

        final List<Integer> domainIds = domainService.getDomainIdsByName(rootDomainName);
        return domainIds == null
                ? userRepository.findByUsernameAndClientTypeAndSuperDomain(userName, clientType)
                .map(userMapper::userEntityToUserWithPassword)
                .orElse(null)
                : userRepository.findByUsernameAndClientTypeAndSubDomains(userName, clientType, domainIds)
                .map(userMapper::userEntityToUserWithPassword)
                .orElse(null);
    }

    private boolean isUserUnique(final Integer userDomainId, final String username, final ClientType clientType) {
        final String userDomainName = domainService.getDomainNameById(userDomainId);
        if ((userDomainName == null || userDomainName.isEmpty()) && isSubDomainNewUser(userDomainName, userDomainId)) {
            throw new FriendlyUnauthorizedUserException(DOMAIN_NOT_FOUND, userDomainName);
        }
        return !userRepository.existsByUsernameAndDomainIdAndClientType(username, userDomainId, clientType);
    }

    private boolean isUserNotUnique(final Integer userDomainId, final Long userId) {
        final String userDomainName = domainService.getDomainNameById(userDomainId);
        if ((userDomainName == null || userDomainName.isEmpty()) && isSubDomainNewUser(userDomainName, userDomainId)) {
            throw new FriendlyUnauthorizedUserException(DOMAIN_NOT_FOUND, userDomainName);
        }
        return userRepository.existsByUsernameAndDomainId(userServiceHelper.getUser(userId).getUsername(), userDomainId);
    }

    private boolean isSubDomainNewUser(final String userDomainName, final Integer userDomainId) {
        final String userDomainFullName = domainService.getDomainNameById(userDomainId);
        if (userDomainFullName != null) {
            if (userDomainFullName.equals(userDomainName)) {
                return true;
            }
            if (userDomainFullName.contains(".")) {
                final String[] domainTreeNames = userDomainFullName.split("\\.");
                return Arrays.asList(domainTreeNames).contains(userDomainName);
            }
        }
        return false;
    }

    /**
     * Get User by id
     * USE ONLY FOR AUTH
     *
     * @return user entity
     */
    public UserResponse getUserByIdWithoutDomain(final Long userId, final String zoneId) {
        return userMapper.userEntityToUser(userServiceHelper.getUser(userId), zoneId);
    }

    /**
     * Create/Update user
     *
     * @return created user
     */
    @Transactional
    public UserResponse createOrUpdateUser(final String token, final UserRequest user) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserEntity userEntity = userMapper.userToUserEntity(user, clientType);

        if (isBlockedOrExpired(user)) {
            sessionService.killSessionsByUserId(user.getId());
        }

        Optional<UserGroupEntity> userGroup = userGroupRepository.findById(user.getUserGroupId());
        if(userGroup.isPresent()) {
            ClientType clientTypeOfUserGroup = userGroup.get().getClientType();
            if(!session.getClientType().equals(clientTypeOfUserGroup)) {
                throw new FriendlyIllegalArgumentException(CLIENT_TYPES_ARE_NOT_COMPATIBLE);
            }
        } else {
            throw new FriendlyEntityNotFoundException(USER_GROUP_NOT_FOUND, user.getUserGroupId());
        }

        final boolean isNew = user.getId() == null;
        if (isNew) {
            //Create user
            if (!isUserUnique(user.getDomainId(), user.getUsername(), clientType)) {
                throw new FriendlyIllegalArgumentException(USER_NOT_UNIQUE);
            }
            userEntity.setPassword(encodePassword(userEntity.getPassword()));
        } else {
            //Update user
            UserEntity previousUser = userServiceHelper.getUser(user.getId());

            if (previousUser.getDomainId().equals(user.getDomainId())) {
                update(user, session, clientType, userEntity, previousUser);
            } else {
                if (isUserNotUnique(user.getDomainId(), user.getId())) {
                    throw new FriendlyIllegalArgumentException(USER_NOT_UNIQUE);
                }
                update(user, session, clientType, userEntity, previousUser);
            }
        }
        userEntity.setLastChangePassword(Instant.now());

        final UserResponse userResponse = userMapper.userEntityToUser(
                userRepository.saveAndFlush(userEntity), session.getZoneId());

        if (isNew) {
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(CREATE_NEW_USER)
                    .note(user.getUsername())
                    .build());
            wsSender.sendUserEvent(clientType, CREATE, userResponse);
        } else {
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(EDIT_USER_SETTING)
                    .note(user.getUsername())
                    .build());
            wsSender.sendUserEvent(clientType, UPDATE, userResponse);
        }
        return userResponse;
    }

    private void update(UserRequest user, Session session, ClientType clientType, UserEntity userEntity, UserEntity previousUser) {
        final String currentPass = previousUser.getPassword();

        if (StringUtils.isBlank(userEntity.getPassword())) {
            userEntity.setPassword(currentPass);
        } else {
            userEntity.setPassword(encodePassword(userEntity.getPassword()));

            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(PASSWORD_CHANGE)
                    .note(user.getUsername())
                    .build());
        }
    }

    /**
     * Delete users by user ids
     */
    @Transactional
    public void deleteUsers(final String token, final Set<Long> userIds) {
        final Session session = jwtService.getSession(token);
        List<String> sessionHashes = sessionService.getActiveSessionHashes(token, new ArrayList<>(userIds));

        userIds.forEach(id -> {
                    try {
                        UserEntity userEntity = userServiceHelper.getUser(id);
                        userRepository.deleteById(id);
                        statisticService.addUserLogAct(UserActivityLog.builder()
                                .userId(session.getUserId())
                                .clientType(session.getClientType())
                                .activityType(DELETE_USER)
                                .note(userEntity.getUsername())
                                .build());
                        wsSender.sendUserEvent(session.getClientType(), DELETE, id);
                    } catch (FriendlyEntityNotFoundException e) {
                        log.debug(e.getMessage());
                    }
                }
        );

        sessionService.killSessions(sessionHashes);
    }

    public FTPage<UserResponse> getAllUsers(final String token,
                                            final AllUsersBody body) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String zoneId = session.getZoneId();
        final List<Integer> pageNumbers = body.getPageNumbers();
        final Integer pageSize = body.getPageSize();
        final String searchParam = body.getSearchParam();
        final Boolean searchExact = body.getSearchExact();
        final List<FieldSort> sorts = body.getSorts();

        String[] properties = sorts != null && !sorts.isEmpty()
                ? ArrayUtils.add(sorts.stream().map(FieldSort::getField).toArray(String[]::new), "id")
                : new String[]{"id"};
        final String firstProperty = properties[0];
        properties = ArrayUtils.removeElement(properties, "domain.name");
        properties = ArrayUtils.removeElement(properties, "status");

        final Sort.Direction dir = sorts != null && !sorts.isEmpty()
                ? Sort.Direction.valueOf(sorts.get(0).getDirection().name()) : Sort.Direction.DESC;
        final int size = pageSize != null && pageSize > 0 ? pageSize : Integer.MAX_VALUE;
        final List<Pageable> pageables = getPageables(pageNumbers, properties, dir, size);
        final List<Integer> domainIds;


        if (user.getDomainId() == null || user.getDomainId() == 0) {
            domainIds = domainService.getDomains().stream()
                    .map(AbstractEntity::getId)
                    .collect(Collectors.toList());

            if (!domainIds.contains(0)) {
                domainIds.add(0);
            }

            user.setDomainId(0);
        } else {
            domainIds = domainService.getDomainIdByUserId(user.getId())
                    .map(domainService::getChildDomainIds)
                    .orElse(null);
        }


        if (firstProperty.equals("domain.name")) {
            return getAllUserGroupByDomain(pageNumbers, clientType, dir, size, searchParam, searchExact, pageables,
                    zoneId, user.getDateFormat(), user.getTimeFormat(), user.getDomainId(), user.getId(),
                    user.getUsername().equalsIgnoreCase("admin"));
        } else if (firstProperty.equals("status")) {
            return getAllUsersFilterByStatus(clientType, dir, searchParam, searchExact, pageables, zoneId,
                    user.getDateFormat(), user.getTimeFormat(), domainIds, user.getDomainId());
        } else {
            return getAllUsers(clientType, searchParam, searchExact, pageables, zoneId, user.getDateFormat(),
                    user.getTimeFormat(), domainIds, user.getDomainId());
        }
    }

    public FTPage<UserResponse> getAllUsers(final ClientType clientType, final String searchParam,
                                            final Boolean searchExact, final List<Pageable> pageable,
                                            final String zoneId, final String dateFormat, final String timeFormat,
                                            final List<Integer> domainIds,
                                            final Integer userDomainId) {
        final List<Page<UserEntity>> userEntityPage =
                pageable.stream()
                        .map(p -> getUserEntities(clientType, searchParam, searchExact, p, domainIds, userDomainId))
                        .collect(Collectors.toList());

        return getUserPage(userEntityPage, zoneId, dateFormat, timeFormat);
    }

    private Page<UserEntity> getUserEntities(final ClientType clientType,
                                                                             final String searchParam,
                                                                             final Boolean searchExact,
                                                                             final Pageable p,
                                                                             final List<Integer> domainIds,
                                                                             final Integer userDomainId) {
        return searchExact != null && searchExact
                ? userRepository.findAllByClientTypeExact(clientType, searchParam, p, domainIds, userDomainId == 0)
                : userRepository.findAllByClientTypeSearch(clientType,
                searchParam == null ? null : "%" + searchParam + "%", p, domainIds, userDomainId == 0);
    }

    public FTPage<UserResponse> getAllUsersFilterByStatus(final ClientType clientType,
                                                          final Sort.Direction dir,
                                                          final String searchParam,
                                                          final Boolean searchExact,
                                                          final List<Pageable> pageable,
                                                          final String zoneId,
                                                          final String dateFormat, final String timeFormat,
                                                          final List<Integer> domainIds,
                                                          final Integer userDomainId) {
        final Instant now = Instant.now();
        final List<Page<UserEntity>> userEntityPage =
                pageable.stream()
                        .map(p -> getUserEntitiesFilterByStatus(clientType, now, dir,
                                searchParam, searchExact, p, domainIds, userDomainId))
                        .collect(Collectors.toList());

        return getUserPage(userEntityPage, zoneId, dateFormat, timeFormat);
    }

    public List<Long> getUserIdsByClientTypeAndDomainIds(final ClientType clientType, final List<Integer> domainIds) {
        final List<UserEntity> userEntities =
                domainIds.contains(null) || domainIds.contains(0) || domainIds.contains(-1)
                        ? userRepository.findAllByClientType(clientType)
                        : userRepository.findAllByClientTypeAndDomainId(clientType, domainIds);

        return userEntities.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
    }

    public List<UserEntity> getUserEntitiesByDomain(final ClientType clientType, final List<Integer> domainIds) {
        return userRepository.findAllByClientTypeAndDomainId(clientType, domainIds);
    }

    private Page<UserEntity> getUserEntitiesFilterByStatus(final ClientType clientType,
                                                                                           final Instant now,
                                                                                           final Sort.Direction dir,
                                                                                           final String searchParam,
                                                                                           final Boolean searchExact,
                                                                                           final Pageable p,
                                                                                           final List<Integer> domainIds,
                                                                                           final Integer userDomainId) {
        if (dir == Sort.Direction.ASC) {
            return searchExact != null && searchExact
                    ? userRepository.findAllByClientTypeAndStatusFilterASCExact(clientType, searchParam, now, p, domainIds, userDomainId == 0)
                    : userRepository.findAllByClientTypeAndStatusFilterASC(clientType,
                    searchParam == null ? null
                            : "%" + searchParam + "%",
                    now, p, domainIds, userDomainId == 0);
        } else {
            return searchExact != null && searchExact
                    ? userRepository.findAllByClientTypeAndStatusFilterDESCExact(clientType, searchParam, now, p, domainIds, userDomainId == 0)
                    : userRepository.findAllByClientTypeAndStatusFilterDESC(clientType,
                    searchParam == null ? null
                            : "%" + searchParam + "%",
                    now, p, domainIds, userDomainId == 0);
        }
    }

    private FTPage<UserResponse> getUserPage(final List<Page<UserEntity>> userEntityPage,
                                             final String zoneId, final String dateFormat, final String timeFormat) {
        final List<UserResponse> users = userEntityPage.stream()
                .map(Page::getContent)
                .flatMap(u -> userMapper.userEntitiesToUsers(u, zoneId,
                                dateFormat,
                                timeFormat)
                        .stream())
                .collect(Collectors.toList());
        users.forEach(this::setDomain);

        return buildUserPage(userEntityPage, users);
    }

    private FTPage<UserResponse> getAllUserGroupByDomain(final List<Integer> pageNumbers,
                                                         final ClientType clientType,
                                                         final Sort.Direction dir,
                                                         final int size,
                                                         final String searchParam,
                                                         final Boolean searchExact,
                                                         final List<Pageable> pageables,
                                                         final String zoneId,
                                                         final String dateFormat,
                                                         final String timeFormat,
                                                         final Integer userDomainId,
                                                         final Long userId,
                                                         final boolean isAdmin) {
        final int pages = pageNumbers == null || pageNumbers.isEmpty() ? 1 : pageNumbers.size();
        List<DomainEntity> domains = null;
        if (userDomainId == null || userDomainId == 0) {
            domains = domainService.getDomains();
            domains.add(DomainEntity.builder()
                    .id(0)
                    .name("superDomain")
                    .build());
        } else {
            List<Integer> domainIds = domainService.getDomainIdByUserId(userId)
                    .map(domainService::getChildDomainIds)
                    .orElse(null);
            if(domainIds != null) {
                domains = domainIds.stream()
                        .map(id -> DomainEntity.builder()
                                .id(id)
                                .name(domainService.getDomainNameById(id))
                                .build())
                        .collect(Collectors.toList());
            }
        }

        final Map<Integer, String> domainsMap =
                domains.stream()
                        .sorted(getComparing(dir))
                        .collect(Collectors.toMap(DomainEntity::getId, DomainEntity::getName,
                                (u, v) -> {
                                    throw new IllegalStateException(
                                            String.format("Duplicate key %s", u));
                                }, LinkedHashMap::new));

        final List<Page<UserEntity>> userEntityPage =
                pageables.stream().
                        flatMap(p -> domainsMap.keySet().
                                stream()
                                .map(id -> getUserEntitiesByDomain(clientType,
                                        searchParam,
                                        searchExact,
                                        id,
                                        p)))
                        .collect(Collectors.toList());

        final long count = userEntityPage.stream()
                .map(Slice::getNumberOfElements)
                .mapToInt(Integer::valueOf)
                .sum();

        int pagesItems = pages * size;


        final List<UserResponse> users = userEntityPage.stream()
                .flatMap(Streamable::stream)
                .limit(pagesItems)
                .map(u -> userMapper.userEntityToUser(u, zoneId, dateFormat, timeFormat))
                .map(user -> user.toBuilder()
                        .domain(DomainSimple.builder()
                                .id(user.getDomainId() != null ? user.getDomainId() : 0)
                                .name(domainsMap.get(user.getDomainId() != null
                                        ? user.getDomainId() : 0))
                                .build())
                        .build())
                .filter(u -> isAdmin && u.getUsername().equalsIgnoreCase("admin"))
                .collect(Collectors.toList());

        final FTPage<UserResponse> userPage = new FTPage<>();
        return userPage.toBuilder()
                .items(users)
                .pageDetails(FTPageDetails.builder()
                        .pageItems(users.size())
                        .totalPages(size != 0 && size != Integer.MAX_VALUE
                                ? (int) Math.ceil((double) count / size)
                                : null)
                        .totalItems(count)
                        .build())
                .build();
    }

    private Page<UserEntity> getUserEntitiesByDomain(final ClientType clientType,
                                                                                     final String searchParam,
                                                                                     final Boolean searchExact,
                                                                                     final Integer domainId,
                                                                                     final Pageable p) {
        return searchExact != null && searchExact
                ? userRepository.findAllByClientTypeAndDomainIdExact(clientType, domainId, searchParam, p)
                : userRepository.findAllByClientTypeAndDomainId(clientType, domainId,
                searchParam == null ? null : "%" + searchParam + "%",
                p);
    }

    private Comparator<DomainEntity> getComparing(final Sort.Direction dir) {
        return dir == Sort.Direction.ASC
                ? Comparator.nullsLast(Comparator.comparing(DomainEntity::getName,
                Comparator.nullsLast(Comparator.naturalOrder())))
                : Comparator.nullsLast(Comparator.comparing(DomainEntity::getName,
                Comparator.nullsLast(Comparator.reverseOrder())));
    }

    private List<Pageable> getPageables(final List<Integer> pageNumbers,
                                        final String[] finalProperties,
                                        final Sort.Direction dir,
                                        final int size) {

        if (pageNumbers == null) {
            return Collections.singletonList(PageRequest.of(0, size, dir, finalProperties));
        } else {
            return pageNumbers.stream()
                    .map(page -> PageRequest.of(page != null && page > 1 ? page - 1 : 0,
                            size, dir, finalProperties))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get User by Id
     *
     * @param userId id of user
     * @return user entity
     */
    public UserResponse getUser(final String token, final Long userId) {
        final Session session = jwtService.getSession(token);

        return Objects.nonNull(userId)
                ? getUser(userId, session.getZoneId(), session.getClientType())
                : getUser(session.getUserId(), session.getZoneId(),session.getClientType());
    }

    public UserResponse getUser(final Long userId, final String zoneId) {
        final UserResponse user = userMapper.userEntityToUser(userServiceHelper.getUser(userId), zoneId);
        setDomain(user);
        return user;
    }

    public UserResponse getUser(final Long userId, final String zoneId, final ClientType clientType) {
        UserEntity userEntity = userServiceHelper.getUser(userId);
        checkIsLastChangePasswordSet(userEntity);
        final UserResponse user = userMapper.userEntityToUser(userEntity, zoneId);
        if(!user.getClientType().equals(clientType)) {
            throw new FriendlyIllegalArgumentException(CLIENT_TYPES_ARE_NOT_COMPATIBLE);
        }
        checkIsPasswordExpired(user, userEntity.getLastChangePassword());
        setDomain(user);
        return user;
    }

    public void checkIsLastChangePasswordSet(UserEntity userEntity) {
        Instant lastChangePassword = userEntity.getLastChangePassword();
        if(lastChangePassword == null) {
            userEntity.setLastChangePassword(Instant.now());
            userRepository.save(userEntity);
        }
    }
    private void checkIsPasswordExpired(UserResponse user, Instant lastChangePassword) {
        if (lastChangePassword == null) {
            return;
        }
        if(Boolean.FALSE.equals(user.getIsChangePassword())) {
            user.setIsChangePassword(isPasswordExpired(user.getClientType(), lastChangePassword));
        }
    }

    private boolean isPasswordExpired(ClientType clientType, Instant lastChangePassword) {
        Optional<String> passwordDaysValidOptional = interfaceService.getInterfaceValue(clientType,
                PASSWORD_DAYS_VALID.getValue());
        Integer passwordDaysValid = passwordDaysValidOptional
                .map(Integer::valueOf)
                .orElse(null);

        if (passwordDaysValid == null || passwordDaysValid == 0) {
            return false;  // Password expiration is disabled or value not found
        }

        long daysSinceLastChange = ChronoUnit.DAYS.between(lastChangePassword, Instant.now());

        return daysSinceLastChange >= passwordDaysValid;
    }

    private void setDomain(final UserResponse user) {
        user.setDomain(DomainSimple.builder()
                .id(user.getDomainId() != null ? user.getDomainId() : -1)
                .name(domainService.getDomainNameById(user.getDomainId()))
                .build());
    }

    private String encodePassword(final String password) {
        return passwordEncoder.encode(password);
    }

    private FTPage<UserResponse> buildUserPage(
            final List<Page<UserEntity>> userEntityPage,
            final List<UserResponse> users) {
        final FTPage<UserResponse> userPage = new FTPage<>();
        return userPage.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(userEntityPage))
                .items(users)
                .build();
    }

    private boolean isBlockedOrExpired(UserRequest userRequest) {
        return userRequest.getBlocked() != null && userRequest.getBlocked()
                || sessionService.isExpired(userRequest.getExpireDateIso());
    }

    public void updateUserLoginDetails(Long userId, Integer failedAttempts){
       userRepository.setFailedAttemptsAndLastLogin(userId, failedAttempts, Instant.now());
    }

    @Transactional
    public void updateIsChangePasswordFieldForUser(Long userId, Boolean isChangePassword){
        userRepository.setIsUserChangePassword(userId, isChangePassword);
    }

    public boolean sendResetPasswordLink(final RestorePasswordRequest restorePasswordRequest) {
        final UserEntity userEntity = userRepository.findByUsernameAndEmail(restorePasswordRequest.getUsername(),
                restorePasswordRequest.getEmail()).orElseThrow(() ->
                new FriendlyEntityNotFoundException(USERNAME_AND_EMAIL_NOT_FOUND, restorePasswordRequest.getUsername(),
                restorePasswordRequest.getEmail()));
        ClientType clientType = userEntity.getClientType();
        try {
            JavaMailSender mailSender = mailConfig.getMailSender(clientType, "Reset password");
            final Long id = userEntity.getId();
            long linkExpirationMls = Long.parseLong(interfaceService.getInterfaceValue(userEntity.getClientType(), "PasswordResetURLLifetime")
                    .orElseThrow(() -> new FriendlyEntityNotFoundException(INTERFACE_VALUE_NOT_FOUND, "PasswordResetURLLifetime"))) * 60000L;
            final long expirationMls = System.currentTimeMillis() + linkExpirationMls;
            final String encryptedLicense = LicenseUtils.encryptLicense(id + "\t" + expirationMls);

            if (encryptedLicense == null) {
                throw new IllegalArgumentException("Encryption failed: LicenseUtils.encryptLicense returned null.");
            }

            final String key = URLEncoder.encode(encryptedLicense, "UTF-8");
            final StringBuilder resetMsg = new StringBuilder("Reset password. Link ")
                    .append(restorePasswordRequest.getEndpoint())
                    .append("?key=")
                    .append(key);

            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEntity.getEmail());
            message.setText(resetMsg.toString());
            mailSender.send(message);

            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(id)
                    .clientType(clientType)
                    .activityType(FORGOT_PASSWORD)
                    .note(userEntity.getUsername())
                    .build());
            wsSender.sendUserEvent(clientType, UPDATE, userEntity);
            return true;
        } catch (MailException e) {
            throw new FriendlyEntityNotFoundException(EMAIL_ERROR);
        } catch (Exception e) {
            throw new FriendlyEntityNotFoundException(RESTORE_PASSWORD_FAILED);
        }
    }
}
