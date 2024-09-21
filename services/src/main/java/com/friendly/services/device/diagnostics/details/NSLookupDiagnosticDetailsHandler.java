package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.util.DiagnosticParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.NS_LOOKUP_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.DNS_SERVER;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.HOST_NAME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.INTERFACE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NS_LOOK_UP_HOP_ANSWER;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NS_LOOK_UP_HOP_DOMAIN;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NS_LOOK_UP_HOP_RESPONSE_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NS_LOOK_UP_HOP_TYPE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NUMBER_OF_REPETITIONS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.RESULT_NUMBER_OF_ENTRIES;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.SUCCESS_COUNT;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIMEOUT;
public class NSLookupDiagnosticDetailsHandler extends DiagnosticDetailsHandler {
    public NSLookupDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }

    @Override
    public DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details) {
        List<DiagnosticParam> diagnosticParams = Arrays.asList(SUCCESS_COUNT, RESULT_NUMBER_OF_ENTRIES, TIMEOUT,
                INTERFACE, NUMBER_OF_REPETITIONS, HOST_NAME, DNS_SERVER);
        for (DiagnosticParam param : diagnosticParams) {
            details.add(getDiagnosticUtil().getDiagnosticParam(id, param));
        }

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(NS_LOOKUP_DIAGNOSTIC.getName())
                .details(details)
                .table(getTables(id))
                .build();
    }

    private List<Map<String, String>> getTables(Long id) {
        Map<String, String> paramNameValueMap = new HashMap<>();
        Stream.of(NS_LOOK_UP_HOP_RESPONSE_TIME, NS_LOOK_UP_HOP_TYPE, NS_LOOK_UP_HOP_ANSWER, NS_LOOK_UP_HOP_DOMAIN)
                .forEach(parameter ->
                        paramNameValueMap.put(parameter.getName(),
                                getDiagnosticUtil().getDiagnosticParam(id, parameter).getValue()));
        List<Map<String, String>> table = new ArrayList<>();
        table.add(paramNameValueMap);
        return table;
    }
}
