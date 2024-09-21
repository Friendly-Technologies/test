package com.friendly.commons.models.settings.security;

import static com.friendly.commons.models.settings.security.auth.SecurityModeType.BASIC;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.NO_SEC;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.PSK;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.PUBLIC_KEY;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.X509;
import com.friendly.commons.models.settings.security.auth.SecurityModeType;

import java.util.Arrays;
import java.util.List;

/**
 * Enum that defines a Security MTP types
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public enum UnderlyingProtocolType {

    COAP(Arrays.asList(PSK, X509, NO_SEC, PUBLIC_KEY)),
    MQTT(Arrays.asList(BASIC, X509, NO_SEC)),
    STOMP(Arrays.asList(BASIC, X509, NO_SEC)),
    WEB_SOCKET(Arrays.asList(X509, NO_SEC)),
    ;

    private final List<SecurityModeType> securityTypes;

    UnderlyingProtocolType(final List<SecurityModeType> securityTypes) {
        this.securityTypes = securityTypes;
    }

    public List<SecurityModeType> getSecurityTypes() {
        return securityTypes;
    }
}
