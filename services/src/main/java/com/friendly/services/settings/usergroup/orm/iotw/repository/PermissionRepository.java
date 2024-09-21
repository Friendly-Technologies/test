package com.friendly.services.settings.usergroup.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link PermissionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface PermissionRepository extends BaseJpaRepository<PermissionEntity, Long> {

    @Query("SELECT p.id FROM PermissionEntity p WHERE p.clientType <> :clientType")
    List<Long> findIdsByClientTypeNot(final ClientType clientType);

    @Query("SELECT p.id FROM PermissionEntity p WHERE p.clientType = :clientType and p.id not in :ids " +
            "and (p.parentId not in :customIds or p.parentId is null)")
    List<Long> findIdsByClientTypeAndIdNotIn(final ClientType clientType, final List<Long> ids,
                                             final List<Long> customIds);

    @Query("SELECT p.id FROM PermissionEntity p WHERE p.clientType = :clientType and p.type = 'frame-custom'")
    List<Long> findFrameCustomIds(final ClientType clientType);

    List<PermissionEntity> findAllByClientTypeAndPath(final ClientType clientType, final String path);

    Optional<PermissionEntity> findAllByClientTypeAndPathAndType(ClientType clientType, String path, String type);

    List<PermissionEntity> findAllByClientTypeAndPathInAndType(ClientType clientType, List<String> path, String type);

    List<PermissionEntity> findAllByClientTypeAndParentId(final ClientType clientType, final Long parentId);

    Optional<PermissionEntity> findByClientTypeAndNameAndParentId(final ClientType clientType, final String name,
                                                                  final Long ParentId);

    @Modifying
    @Query("DELETE FROM PermissionEntity p WHERE p.id IN :ids AND p.clientType = :clientType")
    void deletePermissions(final ClientType clientType, final List<Long> ids);

    @Modifying
    @Query("DELETE FROM PermissionEntity p WHERE p.parentId IN :ids AND p.clientType = :clientType")
    void deletePermissionsByParentId(final ClientType clientType, final List<Long> ids);

}
