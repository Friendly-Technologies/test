package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

import static com.friendly.commons.models.device.ProtocolType.USP;

public class UDPEchoDiagnosticStrategy implements DiagnosticRootStrategy {
    @Override
    public String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService) {
        if (protocol == USP) {
            if(!parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.UDPEchoDiagnostics()")
                    || !parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.IPv4UDPEchoDiagnosticsSupported")
                    || !parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.IPv6UDPEchoDiagnosticsSupported")) {
                return null;
            } else {
                return parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.UDPEchoDiagnostics()") ?
                        "Device.IP.Diagnostics.UDPEchoDiagnostics()." : null;
            }
        } else {
            boolean exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.UDPEchoDiagnostics.");
            return exist
                    ? "Device.IP.Diagnostics.UDPEchoDiagnostics."
                    : null;
        }
    }

    @Override
    public String getDiagnosticRootByProductClass(ProtocolType protocol, Long groupId, TemplateService templateService) {
        if (protocol == USP) {
            return null;
        } else {
            boolean exist = templateService.isParamExist(groupId, "Device.IP.Diagnostics.UDPEchoDiagnostics.");
            return exist
                    ? "Device.IP.Diagnostics.UDPEchoDiagnostics."
                    : null;
        }
    }
}
