package com.friendly.commons.models.device.provision;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Model that represents API version of Provision
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AbstractProvisionRequest.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = ProvisionType.class,
        visible = true)
@JsonSubTypes({
        /* Names for subtype mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "PARAMETERS", value = ProvisionParameterRequest.class),
        @JsonSubTypes.Type(name = "RPC", value = ProvisionRpcRequest.class),
        @JsonSubTypes.Type(name = "OBJECTS", value = ProvisionObjectRequest.class),
        @JsonSubTypes.Type(name = "DOWNLOAD", value = ProvisionDownloadRequest.class)
})
public abstract class AbstractProvisionRequest implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "type";

    private ProvisionType type;
    private Long id;
    private Integer priority;
}
