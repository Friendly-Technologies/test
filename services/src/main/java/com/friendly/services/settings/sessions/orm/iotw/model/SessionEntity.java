package com.friendly.services.settings.sessions.orm.iotw.model;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.friendly.commons.models.auth.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Model that represents persistence version of Session
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode
@Entity
@Table(name = "iotw_session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity implements Serializable {

    @Id
    @Column(name = "session_hash", nullable = false, updatable = false)
    private String sessionHash;

    @Column(name = "notification_identifier", nullable = false, updatable = false)
    private String notificationIdentifier;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "client_type", nullable = false, updatable = false)
    private ClientType clientType;

    @Column(name = "last_activity", nullable = false)
    private Instant lastActivity;

    @Column(name = "logged_at", nullable = false, updatable = false)
    private Instant loggedAt;

    @Column(name = "expire_time", nullable = false, updatable = false)
    private Instant expireTime;

    @Column(name = "zone_id", updatable = false)
    private String zoneId;

}
