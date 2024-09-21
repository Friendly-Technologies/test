package com.friendly.commons.models.settings.security;

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
        property = AbstractSecurity.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = ProtocolSecurityType.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "LWM2M", value = SecurityLWM2M.class),
        @JsonSubTypes.Type(name = "MQTT", value = SecurityMQTT.class),
        @JsonSubTypes.Type(name = "USP", value = SecurityUSP.class)
})
public abstract class AbstractSecurity implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "protocolType";

    private Integer id;
    private String mask;
    private String domainName;

}
