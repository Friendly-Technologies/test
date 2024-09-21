package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.LIST_INTEGER;

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
public class ListIntegerItem extends AbstractConfigItem {

    private List<Integer> value;
    private List<Integer> domainSpecificValue;

    @Builder
    public ListIntegerItem(final String id,
                           final String description,
                           final List<Integer> value,
                           final List<Integer> domainSpecificValue,
                           final boolean required,
                           final boolean isEncrypted,
                           final String group) {
        super(id, LIST_INTEGER, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
    }

}
