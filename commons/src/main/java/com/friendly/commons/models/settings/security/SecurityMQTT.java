package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.bootstrap.SecurityType;
import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityMQTT extends AbstractSecurity {

    private MqttSecurityType securityType;
    private String login;
    private String password;
    private String created;
    private Instant createdIso;

    @Builder
    public SecurityMQTT(final Integer id,
                        final String mask,
                        final String domainName,
                        final String created,
                        final Instant createdIso,
                        final MqttSecurityType securityType,
                        final String login,
                        final String password) {
        super(id, mask, domainName);

        this.securityType = securityType;
        this.login = login;
        this.created = created;
        this.createdIso = createdIso;
        this.password = password;
    }

}
