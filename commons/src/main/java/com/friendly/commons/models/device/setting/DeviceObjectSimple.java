package com.friendly.commons.models.device.setting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
public class DeviceObjectSimple implements Serializable {

    private String shortName;
    private String fullName;

    private List<DeviceObjectSimple> items;
    private List<DeviceParameterSimple> parameters;

    @JsonIgnore
    private String parentName;
}
