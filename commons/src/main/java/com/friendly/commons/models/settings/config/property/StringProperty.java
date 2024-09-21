package com.friendly.commons.models.settings.config.property;

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
public class StringProperty extends AbstractConfigProperty {

    private String value;

    @Builder
    public StringProperty(final Long id,
                          final String name,
                          final String fullName,
                          final String description,
                          final Integer domainId,
                          final boolean overridable,
                          final String value,
                          final boolean writable) {
        super(id, STRING, name, fullName, description, domainId, overridable, writable);

        this.value = value;
    }

}
