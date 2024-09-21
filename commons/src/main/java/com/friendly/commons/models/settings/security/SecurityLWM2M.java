package com.friendly.commons.models.settings.security;

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
public class SecurityLWM2M extends AbstractSecurity {

    private ServerType serverType;
    private SecurityModeType securityType;
    private String created;
    private Instant createdIso;
    private String identity;
    private String secretKey;
    private String serverIdentity;

    @Builder
    public SecurityLWM2M(final Integer id,
                         final String mask,
                         final String domainName,
                         final String created,
                         final Instant createdIso,
                         final ServerType serverType,
                         final SecurityModeType securityType,
                         final String identity,
                         final String secretKey,
                         final String serverIdentity) {
        super(id, mask, domainName);
        this.serverType = serverType;
        this.securityType = securityType;
        this.created = created;
        this.createdIso = createdIso;
        this.identity = identity;
        this.secretKey = secretKey;
        this.serverIdentity = serverIdentity;
    }

}
