package com.friendly.commons.models.settings.security.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AbstractAuthSecurity.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = SecurityModeType.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "PSK", value = AuthPsk.class),
        @JsonSubTypes.Type(name = "BASIC", value = AuthBasic.class),
        @JsonSubTypes.Type(name = "X_509", value = AuthX509.class),
        @JsonSubTypes.Type(name = "X509", value = AuthX509USP.class),
        @JsonSubTypes.Type(name = "NO_SEC", value = AuthNoSec.class),
        @JsonSubTypes.Type(name = "PUBLIC_KEY", value = AuthPublicKey.class),
})
public abstract class AbstractAuthSecurity implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "securityMode";

    private SecurityModeType securityMode;

}
