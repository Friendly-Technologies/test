package com.friendly.commons.models.device.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that represents API version of Device History
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceFile implements Serializable {

    private Long id;
    private String fileType;
    private String url;
    private String created;
    private Instant createdIso;
    private String application;
    private String creator;
    private String description;
    private Boolean isManual = false;
    private String link;
    private String fileName;
    private String targetFileName;

    private String state;
    private String completed;
    private Instant completedIso;
}
