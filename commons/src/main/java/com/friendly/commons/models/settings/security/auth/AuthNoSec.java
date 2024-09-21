package com.friendly.commons.models.settings.security.auth;

import static com.friendly.commons.models.settings.security.auth.SecurityModeType.NO_SEC;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthNoSec extends AbstractAuthSecurity {

    @Builder
    public AuthNoSec() {
        super(NO_SEC);
    }

}
