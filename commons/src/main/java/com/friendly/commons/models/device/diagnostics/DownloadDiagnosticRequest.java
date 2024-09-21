package com.friendly.commons.models.device.diagnostics;

import lombok.*;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DownloadDiagnosticRequest extends AbstractDiagnosticRequest {
    private String url;
    private String duration;
    private String offset;
    private String interval;
    private String numberOfConnections;
}
