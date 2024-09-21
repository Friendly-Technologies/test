package com.friendly.commons.models.device.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class DeviceParameterSimple implements Serializable {

    private String shortName;
    private String fullName;
    private String parentName;
    private List<String> possibleValues;
    private TabViewType valueType;
}
