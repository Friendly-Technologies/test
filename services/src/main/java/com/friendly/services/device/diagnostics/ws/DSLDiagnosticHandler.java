package com.friendly.services.device.diagnostics.ws;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil;
import com.friendly.services.device.diagnostics.util.DiagnosticParam;
import com.ftacs.CpeDiagParameterListWS;
import com.ftacs.CpeDiagnosticWS;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

import static com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil.createCpeDiagParameterWS;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DSLDiagnosticHandler implements DiagnosticToDiagnosticWsHandler {
    int protocolTypeId;
    String root;
    CpeDiagParameterListWS inputParams;
    CpeDiagParameterListWS outputParams;
    CpeDiagnosticWS cpeDiagnosticWS;

    @Override
    public void handleDiagnosticToDiagnosticWs() {
        final String diagName = DiagnosticType.DSL_DIAGNOSTIC.getName();
        cpeDiagnosticWS.setCpeDiagName(diagName);

        inputParams.getCPEDiagParameter()
                .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.LOOP_DIAGNOSTICS_STATE.getParamName(), REQUESTED));
        if (protocolTypeId == 5) {
            outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.STATUS.getParamName()));
        }
        outputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.ACTP_SD_DS.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.ACTP_SD_US.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.HLIN_PS_DS.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.QLN_PS_DS.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.ACTA_TP_DS.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.ACTA_TP_US.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.HLIN_SC_DS.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.SNR_PS_DS.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.BITS_PS_DS.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + DiagnosticParam.GAINS_PS_DS.getParamName())));
    }

}

