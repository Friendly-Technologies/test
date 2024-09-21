package com.friendly.commons.models.tree;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "CheckboxTreeParameterValue", value = CheckboxTreeParameterValue.class),
        @JsonSubTypes.Type(name = "PasswordTreeParameterValue", value = PasswordTreeParameterValue.class),
        @JsonSubTypes.Type(name = "SelectTreeParameterValue", value = SelectTreeParameterValue.class),
        @JsonSubTypes.Type(name = "TextboxTreeParameterValue", value = TextboxTreeParameterValue.class)
})
public class AbstractTreeParameterValue implements Serializable {
    String defaultValue;
    private final TreeParameterValueType valueType;
    public AbstractTreeParameterValue(TreeParameterValueType valueType) {
        this.valueType = valueType;
    }
}
