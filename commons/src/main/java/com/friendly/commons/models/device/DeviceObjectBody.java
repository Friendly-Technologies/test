package com.friendly.commons.models.device;

import com.friendly.commons.models.device.setting.DeviceAddObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Device controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceObjectBody implements Serializable {
	private Long deviceId;
    private DeviceAddObjectRequest request;
    
}
