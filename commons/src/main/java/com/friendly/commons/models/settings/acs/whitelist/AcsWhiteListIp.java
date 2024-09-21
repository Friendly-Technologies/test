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
public class AcsWhiteListIp extends AbstractAcsWhiteList {

    private String ipRange;
    private String manufacturer;
    private String model;

}
