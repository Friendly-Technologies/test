package com.friendly.commons.models.device.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSimplifiedParam implements Serializable {

    private String shortName;
    private String fullName;
    private String defaultValue;
    private String description;
    private String remark;
}
