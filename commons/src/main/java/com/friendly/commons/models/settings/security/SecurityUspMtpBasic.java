package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspMtpBasic extends SecurityUspMtp {


    public SecurityUspMtpBasic(Integer id, Integer securityId,
                             UnderlyingProtocolType mtpProtocolType,
                             SecurityModeType securityType,
                             String login, String password) {
        super(id, securityId, mtpProtocolType, securityType);
        this.login = login;
        this.password = password;
    }

    private String login;
    private String password;
}
