package com.friendly.commons.models.settings.security.auth;

import static com.friendly.commons.models.settings.security.auth.SecurityModeType.X509;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuthX509USP extends AbstractAuthSecurity {

    private String pemCertificate;
    private String alias;

    @Builder
    public AuthX509USP(final String pemCertificate,
                       final String alias) {
        super(X509);

        this.pemCertificate = pemCertificate;
        this.alias = alias;
    }

}
