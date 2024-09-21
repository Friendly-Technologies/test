package com.friendly.commons.models.device.diagnostics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UploadDiagnosticRequest extends AbstractDiagnosticRequest {
    private String url;
    private Integer fileSize;
    private String numberOfConnections;
    private String duration;
    private String interval;
    private String offset;
    private String transferMode;
}
