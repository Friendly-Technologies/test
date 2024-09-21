package com.friendly.services.device.info.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.info.orm.iotw.model.DeviceMonitoringEntity;
import com.friendly.services.device.info.orm.iotw.model.MonitoringGraphEntity;
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
public interface DeviceMonitoringGraphRepository extends BaseJpaRepository<MonitoringGraphEntity, Long> {

    List<MonitoringGraphEntity> findAllByMonitoringId(final Long monitoringId);

    Optional<MonitoringGraphEntity> findFirstByMonitoringIdOrderByIdDesc(final Long monitoringId);

    @Transactional
    @Modifying
    @Query("DELETE FROM MonitoringGraphEntity t WHERE t.monitoringId = :monitoringId")
    int deleteMonitoringGraph(final Long monitoringId);

}
