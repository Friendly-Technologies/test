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
public class LoopbackDiagnosticResponse extends DiagnosticTaskActionResponse{
    private Integer repetitions;
    private Integer timeout;
}
