package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.util.TransferType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.UPLOAD_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.BOM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.ENABLE_PER_CONNECTION_RESULTS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.EOM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.INTERFACE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NUMBER_OF_CONNECTIONS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.PROTOCOL_VERSION;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.ROM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TEST_FILE_LENGTH;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIME_BASED_TEST_DURATION;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIME_BASED_TEST_MEASUREMENT_INTERVAL;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIME_BASED_TEST_MEASUREMENT_OFFSET;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TOTAL_BYTES_RECEIVED;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TOTAL_BYTES_SENT;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.UPLOAD_URL;

public class UploadDiagnosticDetailsHandler extends DiagnosticDetailsHandler {
    public UploadDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }

    @Override
    public DiagnosticDetails handleDiagnosticDetails(final Long id, List<DiagnosticDetail> details) {
        final DiagnosticDetail start = getDiagnosticUtil().getDiagnosticParam(id, BOM_TIME);
        final DiagnosticDetail finish = getDiagnosticUtil().getDiagnosticParam(id, EOM_TIME);
        final DiagnosticDetail interfaceD = getDiagnosticUtil().getDiagnosticParam(id, INTERFACE);
        final DiagnosticDetail romTime = getDiagnosticUtil().getDiagnosticParam(id, ROM_TIME);
        final DiagnosticDetail testBytesReceived = getDiagnosticUtil().getDiagnosticParam(id, TEST_FILE_LENGTH);
        final DiagnosticDetail totalBytesSent = getDiagnosticUtil().getDiagnosticParam(id, TOTAL_BYTES_SENT);
        final DiagnosticDetail totalBytesReceived = getDiagnosticUtil().getDiagnosticParam(id, TOTAL_BYTES_RECEIVED);
        final DiagnosticDetail enablePerConnectionResults = getDiagnosticUtil().getDiagnosticParam(id, ENABLE_PER_CONNECTION_RESULTS);
        final DiagnosticDetail timeBasedTestDuration = getDiagnosticUtil().getDiagnosticParam(id, TIME_BASED_TEST_DURATION);
        final DiagnosticDetail protocolVersion = getDiagnosticUtil().getDiagnosticParam(id, PROTOCOL_VERSION);
        final DiagnosticDetail timeBasedTestMeasurementInterval = getDiagnosticUtil().getDiagnosticParam(id, TIME_BASED_TEST_MEASUREMENT_INTERVAL);
        final DiagnosticDetail timeBasedTestMeasurementOffset = getDiagnosticUtil().getDiagnosticParam(id, TIME_BASED_TEST_MEASUREMENT_OFFSET);
        final DiagnosticDetail numberOfConnections = getDiagnosticUtil().getDiagnosticParam(id, NUMBER_OF_CONNECTIONS);
        details.add(start);
        details.add(finish);
        details.add(interfaceD);
        if (StringUtils.isNotBlank(romTime.getValue())) {
            details.add(romTime);
        }
        if (StringUtils.isNotBlank(testBytesReceived.getValue())) {
            details.add(testBytesReceived);
        }
        if (StringUtils.isNotBlank(totalBytesSent.getValue())) {
            details.add(totalBytesSent);
        }
        if (StringUtils.isNotBlank(totalBytesReceived.getValue())) {
            details.add(totalBytesReceived);
        }
        if (StringUtils.isNotBlank(enablePerConnectionResults.getValue())) {
            details.add(enablePerConnectionResults);
        }
        if (StringUtils.isNotBlank(timeBasedTestDuration.getValue())) {
            details.add(timeBasedTestDuration);
        }
        if (StringUtils.isNotBlank(timeBasedTestMeasurementInterval.getValue())) {
            details.add(timeBasedTestMeasurementInterval);
        }
        if (StringUtils.isNotBlank(timeBasedTestMeasurementOffset.getValue())) {
            details.add(timeBasedTestMeasurementOffset);
        }
        if (StringUtils.isNotBlank(numberOfConnections.getValue())) {
            details.add(numberOfConnections);
        }
        if (StringUtils.isNotBlank(protocolVersion.getValue())) {
            details.add(protocolVersion);
        }
        details.add(getDiagnosticUtil().getDiagnosticParam(id, UPLOAD_URL));

        DiagnosticsDetailsUtil.calculateDetails(details, start.getValue(), finish.getValue(),
                testBytesReceived.getValue(), totalBytesSent.getValue(), TransferType.UPLOAD);

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(UPLOAD_DIAGNOSTIC.getName())
                .details(details)
                .build();
    }
}
