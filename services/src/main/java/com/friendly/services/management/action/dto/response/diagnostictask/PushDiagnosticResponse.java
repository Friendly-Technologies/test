package com.friendly.services.management.action.dto.response.diagnostictask;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class PushDiagnosticResponse extends DiagnosticTaskActionResponse{
}
