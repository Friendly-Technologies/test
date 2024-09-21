package com.friendly.services.device.trace.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.trace.orm.acs.model.DeviceTraceLogEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link DeviceTraceLogEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceTraceRepository extends BaseJpaRepository<DeviceTraceLogEntity, Integer> {

    @Query("SELECT CASE WHEN count(t)>0 THEN true else false end FROM DeviceTraceEntity t " +
            "left join CpeEntity c ON t.deviceId = c.id OR c.serial = t.serial " +
            "WHERE c.id = :deviceId")
    Boolean isTraceStarted(final Long deviceId);

    List<DeviceTraceLogEntity> findAllByDeviceIdOrderById(final Long deviceId);
    List<DeviceTraceLogEntity> findAllByDeviceIdOrderByCreated(final Long deviceId);

    List<DeviceTraceLogEntity> findAllByDeviceIdAndIdInOrderById(final Long deviceId, final List<Integer> ids);

    @Transactional
    @Modifying
    long deleteAllByDeviceIdAndIdIn(final Long deviceId, final List<Integer> ids);
}
