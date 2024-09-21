package com.friendly.services.settings.usergroup.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionPK;
import com.friendly.services.settings.usergroup.orm.iotw.model.PermissionStateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link PermissionStateEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface PermissionStateRepository extends BaseJpaRepository<PermissionStateEntity, PermissionPK> {

    @Query("SELECT ps FROM PermissionStateEntity ps WHERE ps.groupId = :groupId")
    List<PermissionStateEntity> getPermissionStates(final Long groupId);

    @Modifying
    @Query("DELETE FROM PermissionStateEntity ps WHERE ps.permissionId IN :permissionIds ")
    void deletePermissionStates(final List<Long> permissionIds);

    void deleteAllByGroupId(final Long groupId);
}
