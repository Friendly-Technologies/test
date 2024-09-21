package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspMtpX509 extends SecurityUspMtp {

    public SecurityUspMtpX509(Integer id, Integer securityId,
                               UnderlyingProtocolType mtpProtocolType,
                               SecurityModeType securityType,
                               String certificate, String customAlias) {
        super(id, securityId, mtpProtocolType, securityType);
        this.customAlias = customAlias;
        this.certificate = certificate;
    }

    private String certificate;
    private String customAlias;
}
