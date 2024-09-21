package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;

import java.util.Arrays;
import java.util.List;

import static com.friendly.commons.models.device.ProtocolType.USP;

public class NeighboringWiFiDiagnosticStrategy implements DiagnosticRootStrategy {

    private static final String DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC = "Device.WiFi.NeighboringWiFiDiagnostic";
    private static final String DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_BRACES =
            DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC + "()";
    private static final String DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_DOT =
            DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC + ".";
    private static final String INTERNET_GATEWAY_DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_DOT =
            "InternetGatewayDevice.WiFi.NeighboringWiFiDiagnostic.";

    @Override
    public String getDiagnosticRoot(ProtocolType protocol, Long deviceId, ParameterService parameterService) {
        if (protocol == USP) {
            return parameterService.isParamExistLike(deviceId, DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_BRACES)
                    ? DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_BRACES
                    : null;
        }
        List<String> wifiRoots = getRootsForWiFiDiagnostic(parameterService.isParamExistLike(deviceId,
                "InternetGatewayDevice.%"));
        for (String path : wifiRoots) {
            if (parameterService.isParamExist(deviceId, path)) {
                return path;
            }
        }
        return null;
    }

    @Override
    public String getDiagnosticRootByProductClass(ProtocolType protocol, Long groupId,
                                                  TemplateService templateService) {
        if (protocol == USP) {
            return templateService.isParamExistLike(groupId, DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_BRACES)
                    ? DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_BRACES
                    : null;
        }
        List<String> wifiRoots = getRootsForWiFiDiagnostic(templateService.isParamExistLike(groupId,
                "InternetGatewayDevice.%"));
        for (String path : wifiRoots) {
            if (templateService.isParamExist(groupId, path)) return path;
        }
        return null;
    }

    private List<String> getRootsForWiFiDiagnostic(boolean isIGD) {
        return isIGD //check, why there create a list with two identical strings
                ? Arrays.asList(INTERNET_GATEWAY_DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_DOT,
                INTERNET_GATEWAY_DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_DOT)
                : Arrays.asList(DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_DOT,
                DEVICE_WIFI_NEIGHBORING_WIFI_DIAGNOSTIC_DOT);
    }

}
