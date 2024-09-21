package com.friendly.commons.models.device.tools;

import com.friendly.commons.models.device.setting.DeviceObjectSimple;
import com.friendly.commons.models.device.setting.DeviceParameterSimple;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceService implements Serializable {

    private String name;
    private Set<DeviceObjectSimple> objects;
    private Set<DeviceParameterSimple> parameters;

}
