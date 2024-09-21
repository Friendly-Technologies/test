package com.friendly.services.device.diagnostics.ws;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.diagnostics.UdpEchoDiagnosticRequest;
import com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil;
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
public class UdpEchoDiagnosticHandler implements DiagnosticToDiagnosticWsHandler {
    int protocolTypeId;
    String root;
    UdpEchoDiagnosticRequest diagnosticRequest;
    CpeDiagParameterListWS inputParams;
    CpeDiagParameterListWS outputParams;
    CpeDiagnosticWS cpeDiagnosticWS;

    @Override
    public void handleDiagnosticToDiagnosticWs() {
        cpeDiagnosticWS.setCpeDiagName(DiagnosticType.UDP_ECHO_DIAGNOSTIC.getName());


        if (protocolTypeId == 5) {
            outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + STATUS.getParamName()));
        } else {
            inputParams.getCPEDiagParameter()
                    .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DIAGNOSTICS_STATE.getParamName(), REQUESTED));
        }

        outputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(CpeDiagParameterUtil.createCpeDiagParameterWS(root + IP_ADDRESS_USED.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + SUCCESS_COUNT.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + FAILURE_COUNT.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + AVERAGE_RESPONSE_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + MINIMUM_RESPONSE_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + MAXIMUM_RESPONSE_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + INDIVIDUAL_PACKET_RESULT_NUMBER_OF_ENTRIES.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + UDP_ECHO_DIAGNOSTICS_MAX_RESULTS.getParamName())));
        if (diagnosticRequest.isResults()) {
            outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + INDIVIDUAL_PACKET_RESULT.getParamName()));
        }

        inputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + PORT.getParamName(), diagnosticRequest.getPort().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + HOST.getParamName(), diagnosticRequest.getHost()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTERFACE.getParamName(), diagnosticRequest.getConnection()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_REPETITIONS.getParamName(),
                                diagnosticRequest.getRepetitions().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + TIMEOUT.getParamName(),
                                diagnosticRequest.getTimeout().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DATA_BLOCK_SIZE.getParamName(),
                                diagnosticRequest.getDataSize().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DSCP.getParamName(),
                                diagnosticRequest.getDscp().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTER_TRANSMISSION_TIME.getParamName(),
                                diagnosticRequest.getTransmission().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + PROTOCOL_VERSION.getParamName(),
                                diagnosticRequest.getProtocolVersion()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + ENABLE_INDIVIDUAL_PACKET_RESULTS.getParamName(),
                                diagnosticRequest.isResults() ? "1" : "0")));
    }

}
