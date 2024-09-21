package com.friendly.commons.models.settings.security.auth;

/**
 * Enum that defines a Security modes
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public enum SecurityModeType {
    PSK(0),
    BASIC(1),
    X_509(2),
    X509(2),
    NO_SEC(3),
    PUBLIC_KEY(4);

    final int ordinal;

    SecurityModeType(final int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

}
