package com.friendly.commons.models.settings.security.auth;

import static com.friendly.commons.models.settings.security.auth.SecurityModeType.PUBLIC_KEY;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuthPublicKey extends AbstractAuthSecurity {

    private String serverKey;
    private String clientKey;
    private String privateKey;

    @Builder
    public AuthPublicKey(final String serverKey,
                         final String clientKey,
                         final String privateKey) {
        super(PUBLIC_KEY);

        this.serverKey = serverKey;
        this.clientKey = clientKey;
        this.privateKey = privateKey;
    }

}
