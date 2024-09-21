package com.friendly.commons.models.file;

import com.friendly.commons.models.device.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileFtpKey implements Serializable {
    private String manufacturer;
    private String model;
    private ProtocolType protocolType;
    private String fileName;
}