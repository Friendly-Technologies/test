package com.friendly.commons.models.device.provision;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

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
        property = AbstractProvision.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = ProvisionType.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "PARAMETERS", value = ProvisionParameter.class),
        @JsonSubTypes.Type(name = "RPC", value = ProvisionRpc.class),
        @JsonSubTypes.Type(name = "OBJECTS", value = ProvisionObject.class),
        @JsonSubTypes.Type(name = "DOWNLOAD", value = ProvisionDownload.class)
})
public abstract class AbstractProvision implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "type";

    private ProvisionType type;
    private Long id;
    private Integer priority;
    private Instant updatedIso;
    private String updated;
    private String application;
    private String user;
}
