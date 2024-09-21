package com.friendly.commons.models.device.diagnostics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Model that represents API version of Device
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NSLookupDiagnosticRequest extends AbstractDiagnosticRequest {
    private String connection;
    private String dns;
    private String host;
    private Integer repetitions;
    private Integer timeout;
}
