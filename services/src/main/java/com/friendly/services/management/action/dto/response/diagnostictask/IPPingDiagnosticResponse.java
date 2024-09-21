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
public class IPPingDiagnosticResponse extends DiagnosticTaskActionResponse{
    private String host;
    private Integer repetitions;
    private Integer dataSize;
    private Integer timeout;
}
