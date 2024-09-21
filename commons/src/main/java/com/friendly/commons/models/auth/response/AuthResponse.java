package com.friendly.commons.models.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AuthResponse {
    private String token;
    private long expirationTimeMs;
    private String notificationIdentifier;
    private String sessionHash;
}
