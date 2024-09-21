package com.friendly.services.uiservices.statistic.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.DeviceActivityType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.info.orm.acs.model.projections.UserOperationsProjection;
import com.friendly.services.uiservices.statistic.orm.iotw.model.DeviceLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link DeviceLogEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceLogRepository extends BaseJpaRepository<DeviceLogEntity, Long> {

    @Query("SELECT l FROM DeviceLogEntity l WHERE l.clientType = :clientType " +
            "AND (:activityType is null or l.activityType = :activityType) " +
            "AND (:from is null or l.date >= :from) " +
            "AND (:to is null or l.date <= :to) " +
            "AND l.userId IN (:userIds) " +
            "AND (:deviceId is null or l.deviceId = :deviceId) " +
            "AND (:serial is null or l.serial = :serial)")
    Page<DeviceLogEntity> findAll(final ClientType clientType, final DeviceActivityType activityType,
                                  final Instant from, final Instant to, final List<Long> userIds,
                                  final Long deviceId, final String serial, final Pageable pageable);

    @Query("SELECT l FROM DeviceLogEntity l WHERE l.clientType = :clientType " +
            "AND (:activityType is null or l.activityType = :activityType) " +
            "AND (:from is null or l.date >= :from) " +
            "AND (:to is null or l.date <= :to) " +
            "AND l.userId IN (:userIds) " +
            "AND (:deviceId is null or l.deviceId = :deviceId) " +
            "AND (:serial is null or l.serial = :serial)")
    List<DeviceLogEntity> findAll(final ClientType clientType, final DeviceActivityType activityType,
                                  final Instant from, final Instant to, final List<Long> userIds,
                                  final Long deviceId, final String serial);

    @Query(nativeQuery = true, value = "SELECT count(l.id) as operations, " +
            "to_char(l.created, 'DD/MM/YYYY HH24') as dateOp, l.user_id as userId " +
            "FROM iotw_device_activity_log l " +
            "WHERE l.client_type = :clientType " +
            "AND (:activityType is null or l.act_type = :activityType) " +
            "AND (:from is null or l.created >= :from) " +
            "AND (:to is null or l.created <= :to) " +
            "AND l.user_id IN (:userIds) " +
            "GROUP BY to_char(l.created, 'DD/MM/YYYY HH24'), l.user_id")
    List<UserOperationsProjection> findByUserIdOracle(final ClientType clientType,
                                                      final DeviceActivityType activityType, final Instant from,
                                                      final Instant to, final List<Long> userIds);

    @Query(nativeQuery = true, value="SELECT count(l.id) as operations, " +
            "DATE_FORMAT(l.created, '%d/%m/%Y %H') as dateOp, l.user_id as userId " +
            "FROM iotw_device_activity_log l " +
            "WHERE l.client_type = :clientType " +
            "AND (:activityType is null or l.act_type = :activityType) " +
            "AND (:from is null or l.created >= :from) " +
            "AND (:to is null or l.created <= :to) " +
            "AND l.user_id IN (:userIds) " +
            "GROUP BY DATE_FORMAT(l.created, '%d/%m/%Y %H'), l.user_id")
    List<UserOperationsProjection> findByUserIdMySql(final Integer clientType, final String activityType,
                                               final Instant from, final Instant to, final List<Long> userIds);
}
