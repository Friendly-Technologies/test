package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.LIST_STRING;

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
public class ListStringItem extends AbstractConfigItem {

    private List<String> value;
    private List<String> domainSpecificValue;

    @Builder
    public ListStringItem(final String id,
                          final String description,
                          final List<String> value,
                          final List<String> domainSpecificValue,
                          final boolean required,
                          final boolean isEncrypted,
                          final String group) {
        super(id, LIST_STRING, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
    }

}
