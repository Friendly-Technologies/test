package com.friendly.services.device.diagnostics.ws;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.diagnostics.IPPingDiagnosticRequest;
import com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil;
import com.ftacs.CpeDiagParameterListWS;
import com.ftacs.CpeDiagnosticWS;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil.createCpeDiagParameterWS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.*;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IPPingDiagnosticHandler implements DiagnosticToDiagnosticWsHandler {
    int protocolTypeId;
    String root;
    IPPingDiagnosticRequest diagnosticRequest;
    CpeDiagParameterListWS inputParams;
    CpeDiagParameterListWS outputParams;
    CpeDiagnosticWS cpeDiagnosticWS;

    @Override
    public void handleDiagnosticToDiagnosticWs() {
        cpeDiagnosticWS.setCpeDiagName(DiagnosticType.IP_PING_DIAGNOSTIC.getName());

        if (protocolTypeId == 5) {
            outputParams.getCPEDiagParameter()
                    .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + STATUS.getParamName()));
        } else {
            inputParams.getCPEDiagParameter()
                    .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DIAGNOSTICS_STATE.getParamName(), REQUESTED));
        }

        inputParams.getCPEDiagParameter().addAll(Arrays.asList(
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + DSCP.getParamName(), "0"),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_REPETITIONS.getParamName(),
                        diagnosticRequest.getRepetitions().toString()),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + HOST.getParamName(), diagnosticRequest.getHost()),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + DATA_BLOCK_SIZE.getParamName(),
                        diagnosticRequest.getDataSize().toString()),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + TIMEOUT.getParamName(),
                        diagnosticRequest.getTimeout().toString()),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTERFACE.getParamName(),
                        StringUtils.strip(diagnosticRequest.getConnection(), "."))));

        outputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(CpeDiagParameterUtil.createCpeDiagParameterWS(root + SUCCESS_COUNT.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + FAILURE_COUNT.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(
                                root + AVERAGE_RESPONSE_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(
                                root + MINIMUM_RESPONSE_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(
                                root + MAXIMUM_RESPONSE_TIME.getParamName())));
    }

}

