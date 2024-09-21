package com.friendly.commons.models.file;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.file.FileActType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class FilesFtpTypesBody implements Serializable {
	private String manufacturer;
    private String model;
    private ProtocolType protocolType;
    private FileActType fileActType;
}
