package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspMtpRaw extends SecurityUspMtp {

    public SecurityUspMtpRaw(Integer id, Integer securityId,
                             UnderlyingProtocolType mtpProtocolType,
                             SecurityModeType securityType,
                             String serverRPK, String clientRPK,
                             String privateRPK) {
        super(id, securityId, mtpProtocolType, securityType);
        this.clientRPK = clientRPK;
        this.serverRPK = serverRPK;
        this.privateRPK = privateRPK;
    }

    private String serverRPK;
    private String clientRPK;
    private String privateRPK;
}
