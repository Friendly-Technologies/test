package com.friendly.commons.models.settings.config.property;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friendly.commons.models.settings.config.ValueType;
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
        property = AbstractConfigProperty.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = ValueType.class,
        visible = true)
@JsonSubTypes({
        /* Names for subtype mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "STRING", value = StringProperty.class),
        @JsonSubTypes.Type(name = "INTEGER", value = IntegerProperty.class),
        @JsonSubTypes.Type(name = "BOOLEAN", value = BooleanProperty.class),
        @JsonSubTypes.Type(name = "SELECTOR", value = SelectorProperty.class)
})
public abstract class AbstractConfigProperty implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "valueType";

    private Long id;
    private ValueType valueType;
    private String name;
    private String fullName;
    private String description;
    private Integer domainId;
    private boolean overridable;
    private boolean writable;

}
