package com.friendly.commons.models.settings.config.property;

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
public class BooleanProperty extends AbstractConfigProperty {

    private Boolean value;

    @Builder
    public BooleanProperty(final Long id,
                           final String name,
                           final String fullName,
                           final String description,
                           final Integer domainId,
                           final boolean overridable,
                           final Boolean value,
                           final boolean writable) {
        super(id, BOOLEAN, name, fullName, description, domainId, overridable, writable);

        this.value = value;
    }

}
