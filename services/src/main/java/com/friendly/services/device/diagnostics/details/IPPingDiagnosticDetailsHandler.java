package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.util.DiagnosticParam;

import java.util.Arrays;
import java.util.List;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.IP_PING_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.*;

public class IPPingDiagnosticDetailsHandler extends DiagnosticDetailsHandler {

    public IPPingDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }

    @Override
    public DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details) {
        List<DiagnosticParam> diagnosticParams = Arrays.asList(SUCCESS_COUNT, FAILURE_COUNT, MINIMUM_RESPONSE_TIME,
                MAXIMUM_RESPONSE_TIME, AVERAGE_RESPONSE_TIME, DATA_BLOCK_SIZE, DSCP, HOST, INTERFACE,
                NUMBER_OF_REPETITIONS, TIMEOUT);
        for (DiagnosticParam param : diagnosticParams) {
            details.add(getDiagnosticUtil().getDiagnosticParam(id, param));
        }

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(IP_PING_DIAGNOSTIC.getName())
                .details(details)
                .build();
    }

}
