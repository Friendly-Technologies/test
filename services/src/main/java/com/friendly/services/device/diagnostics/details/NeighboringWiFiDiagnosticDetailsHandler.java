package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;

import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.NEIGHBORING_WI_FI_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.RESULT_NUMBER_OF_ENTRIES;

public class NeighboringWiFiDiagnosticDetailsHandler extends DiagnosticDetailsHandler {
    public NeighboringWiFiDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }
    @Override
    public DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details) {
        details.add(getDiagnosticUtil().getDiagnosticParam(id, RESULT_NUMBER_OF_ENTRIES));
        List<Map<String, String>> table = getDiagnosticUtil().getDiagnosticParamResult(id, "%Result.%");

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(NEIGHBORING_WI_FI_DIAGNOSTIC.getName())
                .details(details)
                .table(table)
                .build();
    }
}
