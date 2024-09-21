package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.util.DiagnosticParam;

import java.util.Arrays;
import java.util.List;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.DSL_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.*;

public class DSLDiagnosticDetailsHandler extends DiagnosticDetailsHandler {
    public DSLDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }

    @Override
    public DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details) {
        List<DiagnosticParam> diagnosticParams = Arrays.asList(ACTP_SD_DS, ACTP_SD_US, HLIN_PS_DS, QLN_PS_DS,
                ACTA_TP_DS, ACTA_TP_US, HLIN_SC_DS, SNR_PS_DS, BITS_PS_DS, GAINS_PS_DS);
        for (DiagnosticParam param : diagnosticParams) {
            details.add(getDiagnosticUtil().getDiagnosticParam(id, param));
        }

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(DSL_DIAGNOSTIC.getName())
                .details(details)
                .build();
    }
}
