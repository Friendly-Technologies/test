package com.friendly.commons.models.settings.security;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUSP extends AbstractSecurity {

    private String description;
    private Boolean active;
    private IdentifierType identifierType;

    @Builder
    public SecurityUSP(final Integer id,
                       final String mask,
                       final String domainName,
                       final String description,
                       final Boolean active,
                       final IdentifierType identifierType) {
        super(id, mask, domainName);

        this.description = description;
        this.active = active;
        this.identifierType = identifierType;
    }

}
