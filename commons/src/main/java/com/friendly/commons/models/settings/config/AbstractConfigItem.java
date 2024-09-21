package com.friendly.commons.models.settings.config;

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
        property = AbstractConfigItem.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = ValueType.class,
        visible = true)
@JsonSubTypes({
        /* Names for subtype mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "STRING", value = StringItem.class),
        @JsonSubTypes.Type(name = "INTEGER", value = IntegerItem.class),
        @JsonSubTypes.Type(name = "BOOLEAN", value = BooleanItem.class),
        @JsonSubTypes.Type(name = "LIST_INTEGER", value = ListIntegerItem.class),
        @JsonSubTypes.Type(name = "LIST_STRING", value = ListStringItem.class),
        @JsonSubTypes.Type(name = "SELECTOR", value = SelectorItem.class),
        @JsonSubTypes.Type(name = "LIST_PARAMETER", value = ListParameterItem.class),
        @JsonSubTypes.Type(name = "LIST_MANUFACTURER", value = ManufacturerItem.class)
})
public abstract class AbstractConfigItem implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "valueType";

    private String id;
    private ValueType valueType;
    private String description;
    private boolean required;
    private boolean isEncrypted;
    private String group;
}
