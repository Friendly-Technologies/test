package com.friendly.commons.models.device;

import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.device.setting.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class DeviceProvisionBody implements Serializable {
	private Long deviceId;
    private String tabPath;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;
}


