package com.friendly.commons.models.tree;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class TextboxTreeParameterValue extends AbstractTreeParameterValue {
    public TextboxTreeParameterValue() {
        super(TreeParameterValueType.textbox);
    }
}
