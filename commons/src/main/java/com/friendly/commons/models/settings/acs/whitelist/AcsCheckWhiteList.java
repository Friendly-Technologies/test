package com.friendly.commons.models.settings.acs.whitelist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsCheckWhiteList implements Serializable {

    private String serial;
    private WhiteListSerialType typeSerial;

}
