package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.util.DiagnosticParam;

import java.util.*;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.TRACE_ROUTE_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.*;

public class TraceRouteDiagnosticDetailsHandler extends DiagnosticDetailsHandler {
    public TraceRouteDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }

    @Override
    public DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details) {
        List<DiagnosticParam> diagnosticParams = Arrays.asList(DATA_BLOCK_SIZE, DSCP, HOST, INTERFACE, MAX_HOP_COUNT,
                NUMBER_OF_TRIES, RESPONSE_TIME, ROUTE_HOPS_NUMBER_OF_ENTRIES, TIMEOUT, PROTOCOL_VERSION);
        for (DiagnosticParam param : diagnosticParams) {
            DiagnosticDetail detail = getDiagnosticUtil().getDiagnosticParam(id, param);
            if(detail.getValue() != null) {
                details.add(detail);
            }
        }

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(TRACE_ROUTE_DIAGNOSTIC.getName())
                .details(details)
                .table(convertToTable(getDiagnosticUtil().getTraceDiagnosticResults(id)))
                .build();
    }

    private List<Map<String, String>> convertToTable(List<Object[]> queryResults) {
        Map<Integer, Map<String, String>> tempMap = new HashMap<>();

        for (Object[] row : queryResults) {
            Integer parameterNumber = Integer.parseInt(row[0].toString());
            String parameterName = row[1].toString();
            String value = row[2].toString();

            tempMap.computeIfAbsent(parameterNumber, k -> new HashMap<>()).put(parameterName, value);
        }

        return new ArrayList<>(tempMap.values());
    }
}
