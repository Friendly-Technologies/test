package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.INTEGER;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegerItem extends AbstractConfigItem {

    private Integer value;
    private Integer domainSpecificValue;

    @Builder
    public IntegerItem(final String id,
                       final String description,
                       final Integer value,
                       final Integer domainSpecificValue,
                       final boolean required,
                       final boolean isEncrypted,
                       final String group) {
        super(id, INTEGER, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
    }

}
