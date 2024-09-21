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
public class UdpEchoDiagnosticRequest extends AbstractDiagnosticRequest {
    private String host;
    private String connection;
    private String protocolVersion;
    private Integer repetitions;
    private Integer dataSize;
    private Integer timeout;
    private Integer port;
    private Integer dscp;
    private Integer transmission;
    private boolean results;
}
