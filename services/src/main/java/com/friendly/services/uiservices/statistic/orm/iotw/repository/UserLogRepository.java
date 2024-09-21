package com.friendly.services.uiservices.statistic.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.UserActivityType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.statistic.orm.iotw.model.UserLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link UserLogEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface UserLogRepository extends BaseJpaRepository<UserLogEntity, Long> {

    @Query("SELECT l FROM UserLogEntity l WHERE l.clientType = :clientType " +
            "AND (:activityType is null or l.activityType = :activityType) " +
            "AND (:from is null or l.date >= :from) " +
            "AND (:to is null or l.date <= :to) " +
            "AND l.userId IN (:userIds) ")
    Page<UserLogEntity> findAll(final ClientType clientType, final UserActivityType activityType,
                                final Instant from, final Instant to, final List<Long> userIds,
                                final Pageable pageable);

    @Query("SELECT l FROM UserLogEntity l WHERE l.clientType = :clientType " +
            "AND (:activityType is null or l.activityType = :activityType) " +
            "AND (:from is null or l.date >= :from) " +
            "AND (:to is null or l.date <= :to) " +
            "AND l.userId IN :userIds ")
    List<UserLogEntity> findAll(final ClientType clientType, final UserActivityType activityType,
                                final Instant from, final Instant to, final List<Long> userIds);

}

