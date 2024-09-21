package com.friendly.services.device.diagnostics.ws;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.diagnostics.NSLookupDiagnosticRequest;
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
public class NSLookupDiagnosticHandler implements DiagnosticToDiagnosticWsHandler {
    int protocolTypeId;
    String root;
    NSLookupDiagnosticRequest diagnosticRequest;
    CpeDiagParameterListWS inputParams;
    CpeDiagParameterListWS outputParams;
    CpeDiagnosticWS cpeDiagnosticWS;

    @Override
    public void handleDiagnosticToDiagnosticWs() {
        cpeDiagnosticWS.setCpeDiagName(DiagnosticType.NS_LOOKUP_DIAGNOSTIC.getName());

        if (protocolTypeId == 5) {
            outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + STATUS.getParamName()));
        } else {
            inputParams.getCPEDiagParameter()
                    .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DIAGNOSTICS_STATE.getParamName(), REQUESTED));
        }

        outputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(CpeDiagParameterUtil.createCpeDiagParameterWS(root + RESULT.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + SUCCESS_COUNT.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + RESULT_NUMBER_OF_ENTRIES.getParamName())));

        inputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DNS_SERVER.getParamName(), diagnosticRequest.getDns()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + HOST_NAME.getParamName(), diagnosticRequest.getHost()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTERFACE.getParamName(), diagnosticRequest.getConnection()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_REPETITIONS.getParamName(),
                                diagnosticRequest.getRepetitions().toString()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + TIMEOUT.getParamName(),
                                diagnosticRequest.getTimeout().toString())));
    }

}

