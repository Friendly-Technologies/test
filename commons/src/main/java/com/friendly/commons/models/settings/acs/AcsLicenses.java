package com.friendly.commons.models.settings.acs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsLicenses implements Serializable {

    private List<AcsLicense> items;
    private AcsLicense total;

}
