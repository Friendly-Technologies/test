package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

import static com.friendly.commons.models.device.ProtocolType.USP;

public class TraceRouteDiagnosticStrategy implements DiagnosticRootStrategy {
    @Override
    public String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService) {
        if (protocol == USP) {
            if(!parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.TraceRoute()")
                    || !parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.IPv4TraceRouteSupported")
                    || !parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.IPv6TraceRouteSupported")) {
                return null;
            } else {
                return parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.TraceRoute()") ?
                        "Device.IP.Diagnostics.TraceRoute()" : null;
            }
        } else {
            boolean exist = parameterService.isParamExist(deviceId, "InternetGatewayDevice.TraceRouteDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.TraceRouteDiagnostics.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.TraceRoute.");
            if (exist) {
                return "Device.IP.Diagnostics.TraceRoute.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.LAN.TraceRouteDiagnostics.");
            if (exist) {
                return "Device.LAN.TraceRouteDiagnostics.";
            }
            return null;
        }
    }

    @Override
    public String getDiagnosticRootByProductClass(ProtocolType protocol, Long groupId, TemplateService templateService) {
        if (protocol == USP) {
            return null;
        } else {
            boolean exist = templateService.isParamExist(groupId, "InternetGatewayDevice.TraceRouteDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.TraceRouteDiagnostics.";
            }
            exist = templateService.isParamExist(groupId, "Device.IP.Diagnostics.TraceRoute.");
            if (exist) {
                return "Device.IP.Diagnostics.TraceRoute.";
            }
            exist = templateService.isParamExist(groupId, "Device.LAN.TraceRouteDiagnostics.");
            if (exist) {
                return "Device.LAN.TraceRouteDiagnostics.";
            }
            return null;
        }
    }
}
