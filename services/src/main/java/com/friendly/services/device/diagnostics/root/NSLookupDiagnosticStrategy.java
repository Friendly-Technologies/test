package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

import java.util.List;

import static com.friendly.commons.models.device.ProtocolType.USP;

public class NSLookupDiagnosticStrategy implements DiagnosticRootStrategy {
    @Override
    public String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService) {
        if (protocol == USP) {
            return parameterService.isValidUSPVersion(deviceId)
                    ? "Device.DNS.Diagnostics.NSLookupDiagnostics()."
                    : null;
        }
        List<String> params = parameterService.getParamNamesLike(deviceId, "%.NSLookupDiagnostics.");
        return params.isEmpty()
                ? null
                : params.get(0);
    }

    @Override
    public String getDiagnosticRootByProductClass(ProtocolType protocol, Long groupId, TemplateService templateService) {
        List<String> params = templateService.getParamNamesLike(groupId, "%.NSLookupDiagnostics.");
        return params.isEmpty()
                ? null
                : params.get(0);
    }
}
