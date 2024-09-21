package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.util.DiagnosticParam;

import java.util.Arrays;
import java.util.List;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.UDP_ECHO_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.AVERAGE_RESPONSE_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.DATA_BLOCK_SIZE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.DSCP;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.ENABLE_INDIVIDUAL_PACKET_RESULTS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.FAILURE_COUNT;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.HOST;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.INDIVIDUAL_PACKET_RESULT_NUMBER_OF_ENTRIES;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.INTERFACE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.INTER_TRANSMISSION_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.IP_ADDRESS_USED;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.MAXIMUM_RESPONSE_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.MINIMUM_RESPONSE_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NUMBER_OF_REPETITIONS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.PORT;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.PROTOCOL_VERSION;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.SUCCESS_COUNT;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIMEOUT;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.UDP_ECHO_DIAGNOSTICS_MAX_RESULTS;

public class UDPEchoDiagnosticDetailsHandler extends DiagnosticDetailsHandler {
    public UDPEchoDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }

    @Override
    public DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details) {
        List<DiagnosticParam> diagnosticParams = Arrays.asList(AVERAGE_RESPONSE_TIME, DATA_BLOCK_SIZE, DSCP,
                ENABLE_INDIVIDUAL_PACKET_RESULTS, FAILURE_COUNT, HOST, INDIVIDUAL_PACKET_RESULT_NUMBER_OF_ENTRIES,
                INTERFACE, INTER_TRANSMISSION_TIME, IP_ADDRESS_USED, MAXIMUM_RESPONSE_TIME, MINIMUM_RESPONSE_TIME,
                NUMBER_OF_REPETITIONS, PORT, PROTOCOL_VERSION, SUCCESS_COUNT, TIMEOUT,
                UDP_ECHO_DIAGNOSTICS_MAX_RESULTS);
        for (DiagnosticParam param : diagnosticParams) {
            DiagnosticDetail diagnosticParam = getDiagnosticUtil().getDiagnosticParam(id, param);
            if (diagnosticParam.getValue() != null) {
                details.add(diagnosticParam);
            }
        }

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(UDP_ECHO_DIAGNOSTIC.getName())
                .details(details)
                .build();
    }
}
