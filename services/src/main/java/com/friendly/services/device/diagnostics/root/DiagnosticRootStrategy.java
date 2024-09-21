package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

public interface DiagnosticRootStrategy {
    String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService);

    String getDiagnosticRootByProductClass(ProtocolType protocol, Long groupId, TemplateService templateService);
}
