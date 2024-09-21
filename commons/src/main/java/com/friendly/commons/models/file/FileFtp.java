package com.friendly.commons.models.file;

import com.friendly.commons.models.device.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.Instant;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileFtp implements Serializable {
    private String manufacturer;
    private String model;
    private ProtocolType protocolType;
    String domainName;
    private Integer fileTypeId;
    private String fileName;
    private String version;
    private Boolean newest;
    private FileFtpStateEnum state;
    private Long size;
    private String created;
    private Instant createdIso;
    private String fileUrl;
}