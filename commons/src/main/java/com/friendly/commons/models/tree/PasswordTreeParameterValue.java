package com.friendly.commons.models.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class PasswordTreeParameterValue extends AbstractTreeParameterValue {
    public PasswordTreeParameterValue() {
        super(TreeParameterValueType.password);
    }
}
