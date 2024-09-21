package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import com.friendly.commons.models.settings.security.oscore.AeadAlgorithmType;
import com.friendly.commons.models.settings.security.oscore.HmacAlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityLWM2MDetails {
    private Integer id;
    private MaskType maskType;
    private String mask;
    private ServerType serverType;
    private Integer domainId;
    private SecurityModeType securityType;
    private String clientIdentity;
    private String secretKey;
    private String serverIdentity;
    private String masterSecret;
    private String senderId;
    private String recipientId;
    private AeadAlgorithmType aeadAlgorithmType;
    private HmacAlgorithmType hmacAlgorithmType;
    private String masterSalt;
    private Boolean isOscore;
}
