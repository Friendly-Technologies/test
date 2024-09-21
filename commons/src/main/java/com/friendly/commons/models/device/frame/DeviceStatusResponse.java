package com.friendly.commons.models.device.frame;

import com.friendly.commons.models.device.DeviceStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Device status connection to ACS
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusResponse implements Serializable {

    private DeviceStatusType status;

}
