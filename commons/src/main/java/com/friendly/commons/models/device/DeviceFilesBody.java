package com.friendly.commons.models.device;

import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.device.file.FileActType;
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
public class DeviceFilesBody implements Serializable {
	private FileActType fileActType;
    private Long deviceId;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;
}
