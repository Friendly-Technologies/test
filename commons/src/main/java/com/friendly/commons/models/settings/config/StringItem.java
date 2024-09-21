package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.STRING;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringItem extends AbstractConfigItem {

    private String value;
    private String domainSpecificValue;

    @Builder
    public StringItem(final String id,
                      final String description,
                      final String value,
                      final String domainSpecificValue,
                      final boolean required,
                      final boolean isEncrypted,
                      final String group) {
        super(id, STRING, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
    }

}
