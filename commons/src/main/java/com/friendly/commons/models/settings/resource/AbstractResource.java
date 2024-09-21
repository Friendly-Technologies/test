package com.friendly.commons.models.settings.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AbstractResource.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = ProtocolResourceType.class,
        visible = true)
@JsonSubTypes({
        /* Names for subtype mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "LWM2M", value = ResourceLWM2M.class)
})
public abstract class AbstractResource implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "protocolType";

    private Integer id;
    private Integer objectId;
    private String name;
    private String description;
    private ResourceType instanceType;
    private String version;
    private List<ResourceDetailsItem> items;
    private List<ResourceDetails> parameters;
}
