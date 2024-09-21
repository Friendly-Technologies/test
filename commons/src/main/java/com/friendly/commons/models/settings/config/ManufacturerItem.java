package com.friendly.commons.models.settings.config;

import static com.friendly.commons.models.settings.config.ValueType.LIST_MANUFACTURER;

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
public class ManufacturerItem extends AbstractConfigItem {

    private List<Manufacturer> value;
    private List<Manufacturer> domainSpecificValue;

    @Builder
    public ManufacturerItem(final String id,
                            final String description,
                            final List<Manufacturer> value,
                            final List<Manufacturer> domainSpecificValue,
                            final boolean required,
                            final boolean isEncrypted,
                            final String group) {
        super(id, LIST_MANUFACTURER, description, required, isEncrypted, group);

        this.value = value;
        this.domainSpecificValue = domainSpecificValue;
    }

}
