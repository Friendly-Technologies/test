package com.friendly.commons.models.settings.security.auth;

import static com.friendly.commons.models.settings.security.auth.SecurityModeType.BASIC;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuthBasic extends AbstractAuthSecurity {

    private String login;
    private String password;
    private String privateKey;

    @Builder
    public AuthBasic(final String login,
                     final String password) {
        super(BASIC);

        this.login = login;
        this.password = password;
    }

}
