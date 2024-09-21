package com.friendly.commons.models.settings.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityMQTTDetails {
    private Integer id;
    private MaskType maskType;
    private String mask;
    private Integer domainId;
    private MqttSecurityType securityType;
    private String login;
    private String password;
}
