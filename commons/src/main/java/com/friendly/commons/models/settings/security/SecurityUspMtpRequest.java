package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityUspMtpRequest {
    private Integer id;
    private Integer securityId;
    private UnderlyingProtocolType mtpProtocolType;
    private SecurityModeType securityType;
    private String login;
    private String password;
    private String pskId;
    private String pskKey;
    private String certificate;
    private String customAlias;
    private String serverRPK;
    private String clientRPK;
    private String privateRPK;
}
