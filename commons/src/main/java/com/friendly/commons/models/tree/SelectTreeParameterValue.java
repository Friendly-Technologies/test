package com.friendly.commons.models.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class SelectTreeParameterValue extends AbstractTreeParameterValue {
    private List<String> possibleValues;
    public SelectTreeParameterValue() {
        super(TreeParameterValueType.select);
    }
}
