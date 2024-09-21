package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspMtpPSK extends SecurityUspMtp {

    public SecurityUspMtpPSK(Integer id, Integer securityId,
                             UnderlyingProtocolType mtpProtocolType,
                             SecurityModeType securityType,
                             String pskId, String pskKey) {
        super(id, securityId, mtpProtocolType, securityType);
        this.pskId = pskId;
        this.pskKey = pskKey;
    }

    private String pskId;
    private String pskKey;
}
