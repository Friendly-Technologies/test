package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupCompletedEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.DeviceStateProjection;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.DevicesStatusProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link UpdateGroupCompletedEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface UpdateGroupCompletedRepository extends BaseJpaRepository<UpdateGroupCompletedEntity, Integer> {

    Optional<UpdateGroupCompletedEntity> findByUpdateGroupIdAndDeviceId(Integer updateGroupId, Long deviceId);

    @Query("SELECT uc.id, uc.device.serial, uc.state  FROM UpdateGroupCompletedEntity uc " +
            "where uc.ugChildId=:ugChildId")
    Page<DeviceStateProjection> findAllByUgId(Long ugChildId, Pageable p);

    @Query("SELECT uc.id, uc.device.serial, uc.state  FROM UpdateGroupCompletedEntity uc " +
            "where uc.ugChildId=:ugChildId AND lower(uc.device.serial) LIKE lower(:searchParam)")
    Page<DeviceStateProjection> findAllByUgIdAndSerial(Long ugChildId, String searchParam, Pageable p);

    @Query("SELECT uc.state, count(uc)  FROM UpdateGroupCompletedEntity uc where uc.updateGroupId=:updateGroupId GROUP BY uc.state")
    List<DevicesStatusProjection> findDevicesStatus(Integer updateGroupId);
}
