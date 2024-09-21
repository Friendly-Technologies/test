package com.friendly.services.uiservices.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.UNKNOWN_AUTH_TYPE;

@Slf4j
public enum AuthType {
    DATABASE("database"),
    LDAP("ldap"),
    SAML("saml"),
    WINDOWS("windows");

    private static final Map<String, AuthType> FORMAT_MAP;
    private final String value;

    static {
        FORMAT_MAP = Stream.of(AuthType.values())
                .collect(Collectors.toMap(authType -> authType.value, authType -> authType));
    }

    AuthType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AuthType fromValue(String value) {
        if (value == null || value.isEmpty()) {
            log.warn("Authentication type string is null or empty. Using default value: {}", AuthType.DATABASE);
            return AuthType.DATABASE;
        }

        AuthType authType = FORMAT_MAP.get(value.toLowerCase());
        if (authType == null) {
            throw new FriendlyIllegalArgumentException(UNKNOWN_AUTH_TYPE, value);
        }
        return authType;
    }
}
