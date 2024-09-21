package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

import static com.friendly.commons.models.device.ProtocolType.USP;

public class UploadDiagnosticStrategy implements DiagnosticRootStrategy {
    @Override
    public String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService) {
        boolean exist;
        if (protocol == USP) {
            exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.UploadDiagnostics().";
            }
        } else {
            exist = parameterService.isParamExist(deviceId, "InternetGatewayDevice.UploadDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.UploadDiagnostics.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.IP.Diagnostics.UploadDiagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.UploadDiagnostics.";
            }
            exist = parameterService.isParamExist(deviceId, "Device.UploadDiagnostics.");
            if (exist) {
                return "Device.UploadDiagnostics.";
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
                return "Device.IP.Diagnostics.UploadDiagnostics().";
            }
        } else {
            exist = templateService.isParamExist(groupId, "InternetGatewayDevice.UploadDiagnostics.");
            if (exist) {
                return "InternetGatewayDevice.UploadDiagnostics.";
            }
            exist = templateService.isParamExist(groupId, "Device.IP.Diagnostics.UploadDiagnostics.");
            if (exist) {
                return "Device.IP.Diagnostics.UploadDiagnostics.";
            }
            exist = templateService.isParamExist(groupId, "Device.UploadDiagnostics.");
            if (exist) {
                return "Device.UploadDiagnostics.";
            }
        }
        return null;
    }
}
