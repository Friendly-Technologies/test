package com.friendly.commons.models.settings.acs.whitelist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RemoveSerialFromWhiteList implements Serializable {

    private List<String> serials;
    private WhiteListSerialType typeSerial;

}
