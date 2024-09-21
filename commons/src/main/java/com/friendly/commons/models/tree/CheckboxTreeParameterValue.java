package com.friendly.commons.models.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class CheckboxTreeParameterValue extends AbstractTreeParameterValue {
    public CheckboxTreeParameterValue() {
        super(TreeParameterValueType.checkbox);
    }
}
