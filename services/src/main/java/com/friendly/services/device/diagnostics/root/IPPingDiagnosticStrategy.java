package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

import static com.friendly.commons.models.device.ProtocolType.USP;

public class IPPingDiagnosticStrategy implements DiagnosticRootStrategy {
    @Override
    public String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService) {
        boolean exist;
        if (protocol == USP) {
            exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.IPPing().";
            }
        } else {
            exist = parameterService.isParamExist(deviceId, "InternetGatewayDevice.IPPingDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.IPPingDiagnostics.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.IPPing.");
            if (exist) {
                return "Device.IP.Diagnostics.IPPing.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.LAN.IPPingDiagnostics.");
            if (exist) {
                return "Device.LAN.IPPingDiagnostics.";
            }
        }
        return null;
    }

    @Override
    public String getDiagnosticRootByProductClass(ProtocolType protocol, Long groupId, TemplateService templateService) {
        boolean exist;
        if (protocol == USP) {
            exist = templateService.isParamExist(groupId, "Device.IP.Diagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.IPPing().";
            }
        } else {
            exist = templateService.isParamExist(groupId, "InternetGatewayDevice.IPPingDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.IPPingDiagnostics.";
            }
            exist = templateService.isParamExist(groupId, "Device.IP.Diagnostics.IPPing.");
            if (exist) {
                return "Device.IP.Diagnostics.IPPing.";
            }
            exist = templateService.isParamExist(groupId, "Device.LAN.IPPingDiagnostics.");
            if (exist) {
                return "Device.LAN.IPPingDiagnostics.";
            }
        }
        return null;
    }
}
