package com.friendly.commons.models.file;

import com.friendly.commons.models.device.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Model that represents API version of IOT controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FilesFtpBody implements Serializable {
	private Integer fileTypeId;
    private String manufacturer;
    private String model;
    private ProtocolType protocolType;
    private List<Integer> pageNumbers;
    private Integer pageSize;
}
