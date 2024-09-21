package com.friendly.services.device.diagnostics.ws;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.diagnostics.DownloadDiagnosticRequest;
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
public class DownloadDiagnosticHandler implements DiagnosticToDiagnosticWsHandler {
    int protocolTypeId;
    String root;
    DownloadDiagnosticRequest diagnosticRequest;
    CpeDiagParameterListWS inputParams;
    CpeDiagParameterListWS outputParams;
    CpeDiagnosticWS cpeDiagnosticWS;

    @Override
    public void handleDiagnosticToDiagnosticWs() {
        cpeDiagnosticWS.setCpeDiagName(DiagnosticType.DOWNLOAD_DIAGNOSTIC.getName());

        if (protocolTypeId == 5) {
            outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + STATUS.getParamName()));
        } else {
            inputParams.getCPEDiagParameter()
                    .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DIAGNOSTICS_STATE.getParamName(), REQUESTED));
        }

        inputParams.getCPEDiagParameter().addAll(Arrays.asList(
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + DOWNLOAD_URL.getParamName(), diagnosticRequest.getUrl()),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + NUMBER_OF_CONNECTIONS.getParamName(), diagnosticRequest.getNumberOfConnections()),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + TIME_BASED_TEST_DURATION.getParamName(), diagnosticRequest.getDuration()),
                CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTERFACE.getParamName(), "")));

        outputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(CpeDiagParameterUtil.createCpeDiagParameterWS(root + BOM_TIME.getParamName(), ""),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + EOM_TIME.getParamName(), ""),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + ROM_TIME.getParamName(), ""),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + TEST_BYTES_RECEIVED.getParamName(), ""),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + TOTAL_BYTES_RECEIVED.getParamName(), "")));
    }

}

