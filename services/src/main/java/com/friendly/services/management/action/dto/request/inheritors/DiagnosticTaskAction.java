package com.friendly.services.management.action.dto.request.inheritors;

import com.friendly.commons.models.device.diagnostics.AbstractDiagnosticRequest;
import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosticTaskAction extends AbstractActionRequest {
    private AbstractDiagnosticRequest diagnosticRequest;
}
