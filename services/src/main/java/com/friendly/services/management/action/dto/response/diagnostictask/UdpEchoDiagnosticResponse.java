package com.friendly.services.management.action.dto.response.diagnostictask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class
UdpEchoDiagnosticResponse extends DiagnosticTaskActionResponse{
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
