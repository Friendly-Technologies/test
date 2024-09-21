package com.friendly.services.device.diagnostics.ws;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.diagnostics.TraceRouteDiagnosticRequest;
import com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;
import com.ftacs.CpeDiagParameterListWS;
import com.ftacs.CpeDiagnosticWS;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

import static com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil.createCpeDiagParameterWS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.*;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TraceRouteDiagnosticHandler implements DiagnosticToDiagnosticWsHandler {

    Long deviceId;
    Long groupId;
    String root;
    int protocolTypeId;
    TraceRouteDiagnosticRequest diagnosticRequest;
    CpeDiagParameterListWS inputParams;
    CpeDiagParameterListWS outputParams;
    CpeDiagnosticWS cpeDiagnosticWS;
    ParameterService parameterService;
    TemplateService templateService;

    @Override
    public void handleDiagnosticToDiagnosticWs() {
        handleDiagnosticBase();
        if(deviceId != null) {
            if (root.equals("Device.") && isParamDeviceLanExist(deviceId)) {
                outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_ROUTE_HOPS.getParamName()));
            } else {
                outputParams.getCPEDiagParameter()
                        .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + ROUTE_HOPS_NUMBER_OF_ENTRIES.getParamName()));
                inputParams.getCPEDiagParameter()
                        .addAll(Arrays.asList(
                                CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTERFACE.getParamName(),
                                        diagnosticRequest.getConnection()),
                                CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_TRIES.getParamName(),
                                        diagnosticRequest.getRepetitions().toString())));
            }
        }else if(groupId != null) {
            if (root.equals("Device.") && isParamGroupLanExist(groupId)) {
                outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_ROUTE_HOPS.getParamName()));
            } else {
                outputParams.getCPEDiagParameter()
                        .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + ROUTE_HOPS_NUMBER_OF_ENTRIES.getParamName()));
                inputParams.getCPEDiagParameter()
                        .addAll(Arrays.asList(
                                CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTERFACE.getParamName(),
                                        diagnosticRequest.getConnection()),
                                CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_TRIES.getParamName(),
                                        diagnosticRequest.getRepetitions().toString())));
            }
        }
    }

    private boolean isParamGroupLanExist(Long groupId) {
        return templateService.isParamExistLike(groupId, "Device.LAN.%");
    }

    private boolean isParamDeviceLanExist(final Long deviceId) {
        return parameterService.isParamExistLike(deviceId, "Device.LAN.%");
    }

    private void handleDiagnosticBase(){
        cpeDiagnosticWS.setCpeDiagName(DiagnosticType.TRACE_ROUTE_DIAGNOSTIC.getName());

        if (protocolTypeId == 5) {
            outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + STATUS.getParamName()));
        } else {
            inputParams.getCPEDiagParameter()
                    .addAll(Arrays.asList(
                            CpeDiagParameterUtil.createCpeDiagParameterWS(root + DIAGNOSTICS_STATE.getParamName(), REQUESTED),
                            CpeDiagParameterUtil.createCpeDiagParameterWS(root + DSCP.getParamName(), "0")));
        }

        outputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(CpeDiagParameterUtil.createCpeDiagParameterWS(root + RESPONSE_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + ROUTE_HOPS.getParamName())));
        inputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + HOST.getParamName(), diagnosticRequest.getHost()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DATA_BLOCK_SIZE.getParamName(),
                                diagnosticRequest.getDataSize().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + TIMEOUT.getParamName(),
                                diagnosticRequest.getTimeout().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + PROTOCOL_VERSION.getParamName(),
                                diagnosticRequest.getProtocolVersion()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + MAX_HOP_COUNT.getParamName(),
                                diagnosticRequest.getMaxHop().toString())));
    }


}

