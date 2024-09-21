package com.friendly.commons.models.settings.config.property;

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
public class SelectorProperty extends AbstractConfigProperty {

    private String value;
    private List<String> values;

    @Builder
    public SelectorProperty(final Long id,
                            final String name,
                            final String fullName,
                            final String description,
                            final Integer domainId,
                            final boolean overridable,
                            final String value,
                            final List<String> values,
                            final boolean writable) {
        super(id, SELECTOR, name, fullName, description, domainId, overridable, writable);
        this.values = values;
        this.value = value;
    }

}
