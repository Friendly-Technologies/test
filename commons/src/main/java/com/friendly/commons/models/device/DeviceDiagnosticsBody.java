package com.friendly.commons.models.device;

import com.friendly.commons.models.FieldSort;
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
public class DeviceDiagnosticsBody implements Serializable {
	private Long deviceId;
	private List<Integer> pageNumbers;
	private Integer pageSize;
	private List<FieldSort> sorts;

}
