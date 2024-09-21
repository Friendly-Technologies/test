package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.SELECTOR;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectorItem extends AbstractConfigItem {

    private String value;
    private String domainSpecificValue;
    private List<String> values;
    @Builder
    public SelectorItem(final String id,
                        final String description,
                        final String value,
                        final String domainSpecificValue,
                        final List<String> values,
                        final boolean required,
                        final boolean isEncrypted,
                        final String group) {
        super(id, SELECTOR, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
        this.values = values;
    }

}
