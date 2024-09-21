package com.friendly.commons.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that defines a Session
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {

    private String username;
    private String domain;
    private String sessionHash;
    private String lastActivity;
    private String loggedAt;
    private Instant lastActivityIso;
    private Instant loggedAtIso;
    private String duration;

    @JsonIgnore
    private Integer domainId;
}
