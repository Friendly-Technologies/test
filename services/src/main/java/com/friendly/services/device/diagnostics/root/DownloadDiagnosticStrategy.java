package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

import static com.friendly.commons.models.device.ProtocolType.USP;

public class DownloadDiagnosticStrategy implements DiagnosticRootStrategy {
    @Override
    public String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService) {
        boolean exist;
        if (protocol == USP) {
            exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.DownloadDiagnostics().";
            }
        } else {
            exist = parameterService.isParamExist(deviceId, "InternetGatewayDevice.DownloadDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.DownloadDiagnostics.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.DownloadDiagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.DownloadDiagnostics.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.DownloadDiagnostics.");
            if (exist) {
                return "Device.DownloadDiagnostics.";
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
                return "Device.IP.Diagnostics.DownloadDiagnostics().";
            }
        } else {
            exist = templateService.isParamExist(groupId, "InternetGatewayDevice.DownloadDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.DownloadDiagnostics.";
            }
            exist = templateService.isParamExist(groupId, "Device.IP.Diagnostics.DownloadDiagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.DownloadDiagnostics.";
            }
            exist = templateService.isParamExist(groupId, "Device.DownloadDiagnostics.");
            if (exist) {
                return "Device.DownloadDiagnostics.";
            }
        }
        return null;
    }
}
