package com.friendly.commons.models.settings.acs.whitelist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsWhiteListSerial extends AbstractAcsWhiteList {

    private String description;
    private WhiteListSerialType typeSerial;
    private Long serials;

}
