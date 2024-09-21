package com.friendly.services.settings.sessions.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.sessions.orm.iotw.model.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link SessionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface SessionRepository extends BaseJpaRepository<SessionEntity, String> {

    @Modifying
    @Transactional
    @Query("UPDATE SessionEntity s SET s.expireTime = :now WHERE s.sessionHash = :sessionHash")
    void killSession(final String sessionHash, Instant now);

    @Modifying
    @Transactional
    @Query("UPDATE SessionEntity s SET s.expireTime = :now WHERE s.sessionHash in :sessionHash")
    void killSessions(final List<String> sessionHash, Instant now);

    @Query("SELECT s.sessionHash, u.username, u.domainId, s.lastActivity, s.loggedAt FROM SessionEntity s " +
            "INNER JOIN UserEntity u ON s.userId = u.id WHERE s.expireTime > :now AND u.id IN :userIds")
    Page<Object[]> getActiveSessions(List<Long> userIds, Instant now, Pageable pageable);

    @Query("SELECT s.sessionHash FROM SessionEntity s " +
            "INNER JOIN UserEntity u ON s.userId = u.id WHERE s.expireTime > :now AND u.id IN :userIds")
    List<String> getActiveSessionHashes(List<Long> userIds, Instant now);

    @Query("SELECT s.sessionHash, u.username, u.domainId, s.lastActivity, s.loggedAt FROM SessionEntity s " +
            "INNER JOIN UserEntity u ON s.userId = u.id WHERE s.expireTime > :now AND s.clientType = :clientType")
    Page<Object[]> getActiveSessions(Instant now, ClientType clientType, Pageable pageable);

    @Query("SELECT s.sessionHash, u.username, u.domainId, s.expireTime, s.loggedAt FROM SessionEntity s " +
            "INNER JOIN UserEntity u ON s.userId = u.id WHERE (:from is null or s.loggedAt >= :from) " +
            "AND (:to is null or s.lastActivity <= :to) AND u.id IN :userIds")
    Page<Object[]> getSessionStatistic(List<Long> userIds, Instant from, Instant to, Pageable pageable);

    @Query("SELECT s.sessionHash, u.username, u.domainId, s.expireTime, s.loggedAt FROM SessionEntity s " +
            "INNER JOIN UserEntity u ON s.userId = u.id WHERE (:from is null or s.loggedAt >= :from) " +
            "AND (:to is null or s.lastActivity <= :to)")
    Page<Object[]> getSessionStatistic(Instant from, Instant to, Pageable pageable);

    @Query("SELECT u.username, u.domainId, s.lastActivity, s.loggedAt FROM SessionEntity s " +
            "INNER JOIN UserEntity u ON s.userId = u.id WHERE s.expireTime > :now " +
            "AND (:isIdsNull is true or u.id IN :userIds)")
    List<Object[]> getFullActiveSessions(List<Long> userIds, boolean isIdsNull, Instant now);

    @Query("SELECT u.username, u.domainId, s.loggedAt, s.lastActivity FROM SessionEntity s " +
            "INNER JOIN UserEntity u ON s.userId = u.id WHERE (:from is null or s.loggedAt >= :from) " +
            "AND (:to is null or s.lastActivity <= :to) AND (:isIdsNull is true or u.id IN :userIds)")
    List<Object[]> getFullSessionStatistic(List<Long> userIds, boolean isIdsNull, Instant from, Instant to);

    @Query("SELECT s FROM SessionEntity s WHERE s.sessionHash = :sessionHash AND s.expireTime > :now")
    Optional<SessionEntity> getActiveSession(final String sessionHash, Instant now);

    @Query("SELECT s FROM SessionEntity s WHERE s.expireTime > :now")
    List<SessionEntity> getActiveSession(Instant now);

    @Query("SELECT s.notificationIdentifier FROM SessionEntity s " +
            "WHERE s.expireTime > :now AND s.clientType = :clientType")
    List<String> getActiveNotificationsByClientType(ClientType clientType, Instant now);

    @Transactional
    @Modifying
    @Query("DELETE FROM SessionEntity s WHERE s.expireTime > :now and s.userId in (:ids)")
    void deleteAllByUserId(List<Long> ids, Instant now);

    @Transactional
    @Modifying
    @Query("DELETE FROM SessionEntity s WHERE s.expireTime > :now and s.userId = :id")
    void deleteAllByUserId(Long id, Instant now);
}
