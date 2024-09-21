package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.BOOLEAN;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooleanItem extends AbstractConfigItem {

    private Boolean value;
    private Boolean domainSpecificValue;

    @Builder
    public BooleanItem(final String id,
                       final String description,
                       final Boolean value,
                       final Boolean domainSpecificValue,
                       final boolean required,
                       final boolean isEncrypted,
                       final String group) {
        super(id, BOOLEAN, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
    }

}
