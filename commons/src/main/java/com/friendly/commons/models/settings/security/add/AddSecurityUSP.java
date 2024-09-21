package com.friendly.commons.models.settings.security.add;

import static com.friendly.commons.models.settings.security.ProtocolSecurityType.USP;
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
public class AddSecurityUSP extends AddAbstractSecurity {

    private String description;
    private Boolean active;
    private List<SecurityDetailUSP> details;

    @Builder
    public AddSecurityUSP(final Integer id,
                          final Integer domainId,
                          final String mask,
                          final String description,
                          final Boolean active,
                          final List<SecurityDetailUSP> details) {
        super(id, domainId, USP, mask);

        this.description = description;
        this.active = active;
        this.details = details;
    }

}
