package com.friendly.services.uiservices.user.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.UserGroupEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends BaseJpaRepository<UserEntity, Long> {
    List<UserEntity> findAllByClientType(final ClientType clientType);

    long countAllByClientType(final ClientType clientType);

    @Query("SELECT u FROM UserEntity u WHERE u.clientType = :clientType AND (u.domainId IN :domainIds OR :isSuperDomain = true AND u.domainId = null)" +
            " AND (:searchParam is null or (u.name LIKE :searchParam OR u.email LIKE :searchParam OR u.username LIKE :searchParam))")
    Page<UserEntity> findAllByClientTypeSearch(final ClientType clientType, final String searchParam,
                                               final Pageable pageable, final List<Integer> domainIds,
                                               final Boolean isSuperDomain);

    @Query("SELECT u FROM UserEntity u WHERE (u.domainId IN :domainIds OR :isSuperDomain = true AND u.domainId = null)" +
            " AND u.clientType = :clientType AND (:searchParam is null or " +
            "(u.name LIKE :searchParam OR u.email LIKE :searchParam OR u.username LIKE :searchParam)) " +
            "order by CASE WHEN (u.blocked is null or u.blocked = false) and u.expireDate < :now THEN u.expireDate " +
            "ELSE u.blocked END ASC")
    Page<UserEntity> findAllByClientTypeAndStatusFilterASC(final ClientType clientType, final String searchParam,
                                                           final Instant now, final Pageable pageable, final List<Integer> domainIds,
                                                           final Boolean isSuperDomain);

    @Query("SELECT u FROM UserEntity u WHERE (u.domainId IN :domainIds OR :isSuperDomain = true AND u.domainId = null)" +
            "AND u.clientType = :clientType AND (:searchParam is null or " +
            "(u.name = :searchParam OR u.email = :searchParam OR u.username = :searchParam)) " +
            "order by CASE WHEN (u.blocked is null or u.blocked = false) and u.expireDate < :now THEN u.expireDate " +
            "ELSE u.blocked END ASC")
    Page<UserEntity> findAllByClientTypeAndStatusFilterASCExact(final ClientType clientType, final String searchParam,
                                                                final Instant now, final Pageable pageable, final List<Integer> domainIds,
                                                                final Boolean isSuperDomain);

    @Query("SELECT u FROM UserEntity u WHERE (u.domainId IN :domainIds OR :isSuperDomain = true AND u.domainId = null)" +
            " AND u.clientType = :clientType AND (:searchParam is null or " +
            "(u.name LIKE :searchParam OR u.email LIKE :searchParam OR u.username LIKE :searchParam)) " +
            "order by CASE WHEN (u.blocked is null or u.blocked = false) and u.expireDate < :now THEN u.expireDate " +
            "ELSE u.blocked END DESC")
    Page<UserEntity> findAllByClientTypeAndStatusFilterDESC(final ClientType clientType, final String searchParam,
                                                            final Instant now, final Pageable pageable, final List<Integer> domainIds,
                                                            final Boolean isSuperDomain);

    @Query("SELECT u FROM UserEntity u WHERE (u.domainId IN :domainIds OR :isSuperDomain = true AND u.domainId = null)" +
            " AND u.clientType = :clientType AND (:searchParam is null or " +
            "(u.name = :searchParam OR u.email = :searchParam OR u.username = :searchParam)) " +
            "order by CASE WHEN (u.blocked is null or u.blocked = false) and u.expireDate < :now THEN u.expireDate " +
            "ELSE u.blocked END DESC")
    Page<UserEntity> findAllByClientTypeAndStatusFilterDESCExact(final ClientType clientType, final String searchParam,
                                                                 final Instant now, final Pageable pageable, final List<Integer> domainIds,
                                                                 final Boolean isSuperDomain);

    @Query("SELECT u FROM UserEntity u WHERE u.clientType = :clientType AND (u.domainId IN :domainIds OR :isSuperDomain = true AND u.domainId = null) " +
            "AND (:searchParam is null or " + "(u.name = :searchParam OR u.email = :searchParam OR u.username = :searchParam))")
    Page<UserEntity> findAllByClientTypeExact(final ClientType clientType, final String searchParam,
                                              final Pageable pageable, final List<Integer> domainIds,
                                              final Boolean isSuperDomain);

    @Query("SELECT u FROM UserEntity u WHERE u.clientType = :clientType AND " +
            "(u.domainId = :domainId OR (u.domainId is null and :domainId = 0)) AND " +
            "(:searchParam is null or (u.name LIKE :searchParam OR u.email LIKE :searchParam OR u.username LIKE :searchParam))")
    Page<UserEntity> findAllByClientTypeAndDomainId(final ClientType clientType, final Integer domainId,
                                                    final String searchParam, final Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.clientType = :clientType AND " +
            "(u.domainId in (:domainIds))")
    List<UserEntity> findAllByClientTypeAndDomainId(final ClientType clientType, final List<Integer> domainIds);

    @Query("SELECT u FROM UserEntity u WHERE u.clientType = :clientType AND u.domainId = :domainId AND " +
            "(:searchParam is null or (u.name = :searchParam OR u.email = :searchParam OR u.username = :searchParam))")
    Page<UserEntity> findAllByClientTypeAndDomainIdExact(final ClientType clientType, final Integer domainId,
                                                         final String searchParam, final Pageable pageable);

    @Query("SELECT u.id FROM UserEntity u WHERE u.clientType = :clientType AND u.domainId IN :domainIds")
    List<Long> getUserIdsByDomains(final ClientType clientType, final List<Integer> domainIds);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.clientType = :clientType " +
            "AND (u.domainId is null or u.domainId = 0)")
    Optional<UserEntity> findByUsernameAndClientTypeSuperDomain(final String username, final ClientType clientType);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.clientType = :clientType")
    Optional<UserEntity> findByUsernameAndClientType(final String username, final ClientType clientType);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.clientType = :clientType " +
            "AND (u.domainId IN :domainIds OR u.domainId is null OR u.domainId = 0)")
    Optional<UserEntity> findByUsernameAndClientTypeAndDomains(final String username, final ClientType clientType,
                                                               final List<Integer> domainIds);

//    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.email = :email")
    @Query(nativeQuery = true, value = "select * from iotw.iotw_user u where u.email = :email and u.username = :username")
    Optional<UserEntity> findByUsernameAndEmail(final String username, final String email);

    @Query("SELECT ug FROM UserEntity ud INNER JOIN UserGroupEntity ug ON ud.userGroupId = ug.id WHERE ud.id = :id")
    Optional<UserGroupEntity> getUserGroup(final Long id);

    @Query("SELECT ud.domainId FROM UserEntity ud WHERE ud.id = :id")
    Optional<Integer> getDomainId(final Long id);

    @Query("SELECT ud.localeId FROM UserEntity ud WHERE ud.id = :id")
    Optional<String> getLocaleId(final Long id);

    @Modifying
    @Query("UPDATE UserEntity ud SET ud.domainId = :domainId WHERE ud.id = :userId AND ud.domainId is NULL")
    void setDomainToUser(final Long userId, final Integer domainId);

    @Modifying
    @Query("UPDATE UserEntity ud SET ud.isChangePassword = :isPasswordChanged WHERE ud.id = :userId")
    void setIsUserChangePassword(final Long userId, final Boolean isPasswordChanged);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.failedAttempts = :failedAttempts, u.lastLogin = :lastLogin WHERE u.id = :userId")
    int setFailedAttemptsAndLastLogin(final Long userId, final Integer failedAttempts, final Instant lastLogin);

    @Modifying
    @Query("UPDATE UserEntity ud SET ud.domainId = null WHERE ud.domainId = :domainId")
    void deleteDomainFromUser(final Integer domainId);

    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.userGroupId = :userGroupId")
    void deleteUsersByUserGroup(final Long userGroupId);

    @Query(nativeQuery = true, value = "select 'y' from iotw_user where user_group_id = :userGroupId")
    Optional<Object> existsForUserGroup(Long userGroupId);

    @Query(nativeQuery = true, value = "select 'y' from iotw_user where domain_id = :domainId")
    Optional<Object> existsForDomain(Integer domainId);
    @Query(nativeQuery = true,
            value = "SELECT DISTINCT user_group_id " +
                    "FROM iotw_user")
    List<Integer> getAllUserGroupsWithUsers();

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT domain_id " +
                    "FROM iotw_user")
    List<Integer> getAllDomainIdsWithUsers();

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT location_id " +
                    "FROM admin.login")
    List<Integer> getAllDomainIdsWithAcsUsers();

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.clientType = :clientType " +
            "AND (u.domainId is null OR u.domainId = 0)")
    Optional<UserEntity> findByUsernameAndClientTypeAndSuperDomain(final String username, final ClientType clientType);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.clientType = :clientType " +
            "AND u.domainId IN :domainIds")
    Optional<UserEntity> findByUsernameAndClientTypeAndSubDomains(final String username, final ClientType clientType,
                                                                  final List<Integer> domainIds);

    boolean existsByUsernameAndDomainId(String username, Integer userDomainId);

    boolean existsByUsernameAndDomainIdAndClientType(String username, Integer userDomainId, ClientType clientType);
}
