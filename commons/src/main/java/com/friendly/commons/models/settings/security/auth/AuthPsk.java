package com.friendly.commons.models.settings.security.auth;

import static com.friendly.commons.models.settings.security.auth.SecurityModeType.PSK;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuthPsk extends AbstractAuthSecurity {

    private String identity;
    private String privateKey;

    @Builder
    public AuthPsk(final String identity,
                   final String privateKey) {
        super(PSK);

        this.identity = identity;
        this.privateKey = privateKey;
    }

}
