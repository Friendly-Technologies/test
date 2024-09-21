package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;

import java.util.List;

public abstract class DiagnosticDetailsHandler {

    private final DiagnosticsDetailsUtil diagnosticsDetailsUtil;

    protected DiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        this.diagnosticsDetailsUtil = diagnosticsDetailsUtil;
    }

    protected DiagnosticsDetailsUtil getDiagnosticUtil() {
        return diagnosticsDetailsUtil;
    }

    public abstract DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details);
}