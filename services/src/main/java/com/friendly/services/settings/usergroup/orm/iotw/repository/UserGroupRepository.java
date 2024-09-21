package com.friendly.services.settings.usergroup.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.usergroup.orm.iotw.model.UserGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link UserGroupEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface UserGroupRepository extends BaseJpaRepository<UserGroupEntity, Long> {

    @Query("SELECT ug.id, ug.name FROM UserGroupEntity ug WHERE ug.clientType = :clientType AND ug.name <> 'TEMPLATE' ORDER BY ug.name")
    List<Object[]> getSimpleUserGroups(final ClientType clientType);

    @Query("SELECT ug FROM UserGroupEntity ug WHERE ug.clientType = :clientType AND ug.name <> 'TEMPLATE'")
    List<UserGroupEntity> findAllByClientType(final ClientType clientType);

    @Query("SELECT ug FROM UserGroupEntity ug WHERE ug.clientType = :clientType AND ug.name <> 'TEMPLATE'")
    Page<UserGroupEntity> findAllByClientType(final ClientType clientType, final Pageable pageable);

    @Query("SELECT ug FROM UserGroupEntity ug WHERE ug.clientType = :clientType AND ug.name <> 'TEMPLATE' AND ug.name <> 'admin'")
    Page<UserGroupEntity> findNotAdminGroupsByClientType(final ClientType clientType, final Pageable pageable);

    Optional<UserGroupEntity> findUserGroupEntityByClientTypeAndId(final ClientType clientType, final Long id);

    Boolean existsByNameAndClientType(final String name, ClientType type);

    Optional<UserGroupEntity> findUserGroupEntityByClientTypeAndName(final ClientType clientType, final String name);

    @Query("SELECT ug.name FROM UserGroupEntity ug WHERE ug.id = :id")
    Optional<String> getUserGroupNameById(final Long id);

    @Modifying
    @Query("UPDATE UserGroupEntity e SET e.id = -2 WHERE e.name = 'TEMPLATE' AND e.clientType = 1")
    @Transactional
    void updateIdForTemplateMC();

    @Modifying
    @Query("UPDATE UserGroupEntity e SET e.id = -1 WHERE e.name = 'TEMPLATE' AND e.clientType = 0")
    @Transactional
    void updateIdForTemplateSC();
}
