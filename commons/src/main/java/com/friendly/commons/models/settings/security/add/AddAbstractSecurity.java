package com.friendly.commons.models.settings.security.add;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friendly.commons.models.settings.security.ProtocolSecurityType;
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
        property = AddAbstractSecurity.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = ProtocolSecurityType.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "LWM2M", value = AddSecurityLWM2M.class),
        @JsonSubTypes.Type(name = "MQTT", value = AddSecurityMQTT.class),
        @JsonSubTypes.Type(name = "USP", value = AddSecurityUSP.class)
})
public abstract class AddAbstractSecurity implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "protocolType";

    private Integer id;
    private Integer domainId;
    private ProtocolSecurityType protocolType;
    private String mask;

}
