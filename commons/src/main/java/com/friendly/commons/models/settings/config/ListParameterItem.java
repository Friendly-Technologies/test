package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.LIST_PARAMETER;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListParameterItem extends AbstractConfigItem {

    private ListParameter value;
    private ListParameter domainSpecificValue;

    @Builder
    public ListParameterItem(final String id,
                             final String description,
                             final ListParameter value,
                             final ListParameter domainSpecificValue,
                             final boolean required,
                             final boolean isEncrypted,
                             final String group) {
        super(id, LIST_PARAMETER, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
    }
}
