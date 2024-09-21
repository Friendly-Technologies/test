package com.friendly.services.device.info.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.info.orm.iotw.model.DeviceMonitoringEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DeviceMonitoringEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceMonitoringRepository extends BaseJpaRepository<DeviceMonitoringEntity, Long> {

    List<DeviceMonitoringEntity> findAllByDeviceId(final Long deviceId);

    Optional<DeviceMonitoringEntity> findByDeviceIdAndNameId(final Long deviceId, final Long nameId);

    List<DeviceMonitoringEntity> findAllByActiveIsTrue();

    @Transactional
    @Modifying
    @Query("DELETE FROM DeviceMonitoringEntity t WHERE t.deviceId = :deviceId and t.nameId in (:nameIds)")
    int deleteMonitoringParam(Long deviceId, List<Long> nameIds);

    List<DeviceMonitoringEntity> findAllBySessionHash(final String sessionHash);

    @Transactional
    @Modifying
    int deleteBySessionHash(final String sessionHash);

}
