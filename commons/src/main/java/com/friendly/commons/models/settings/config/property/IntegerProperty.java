package com.friendly.commons.models.settings.config.property;

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
public class IntegerProperty extends AbstractConfigProperty {

    private Integer value;
    private Integer minValue;
    private Integer maxValue;

    @Builder
    public IntegerProperty(final Long id,
                           final String name,
                           final String fullName,
                           final String description,
                           final Integer domainId,
                           final boolean overridable,
                           final Integer value,
                           final Integer minValue,
                           final Integer maxValue,
                           final boolean writable) {
        super(id, INTEGER, name, fullName, description, domainId, overridable, writable);

        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

}
