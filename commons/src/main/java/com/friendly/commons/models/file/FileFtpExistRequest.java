package com.friendly.commons.models.file;

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
public class FileFtpExistRequest implements Serializable {
    private String fileName;
    private Integer fileTypeId;
}