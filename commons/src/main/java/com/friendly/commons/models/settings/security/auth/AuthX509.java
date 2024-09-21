package com.friendly.commons.models.settings.security.auth;

import static com.friendly.commons.models.settings.security.auth.SecurityModeType.X_509;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuthX509 extends AbstractAuthSecurity {

    private String serverCertificate;
    private String clientCertificate;
    private String privateKey;

    @Builder
    public AuthX509(final String serverCertificate,
                    final String clientCertificate,
                    final String privateKey) {
        super(X_509);

        this.serverCertificate = serverCertificate;
        this.clientCertificate = clientCertificate;
        this.privateKey = privateKey;
    }

}
