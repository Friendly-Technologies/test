package com.friendly.commons.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.friendly.commons.models.auth.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that defines a Session for Auth
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {

    private Long userId;
    private String sessionHash;
    private String lastActivity;
    private String loggedAt;
    private String expireTime;

    private Instant lastActivityIso;
    private Instant loggedAtIso;
    private Instant expireTimeIso;

    @JsonIgnore
    private ClientType clientType;
    @JsonIgnore
    private String notificationIdentifier;
    @JsonIgnore
    private String zoneId;
}
