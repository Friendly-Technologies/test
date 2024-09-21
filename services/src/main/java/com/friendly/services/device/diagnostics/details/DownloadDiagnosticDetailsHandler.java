package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.util.TransferType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.DOWNLOAD_DIAGNOSTIC;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.*;

public class DownloadDiagnosticDetailsHandler extends DiagnosticDetailsHandler {
    public DownloadDiagnosticDetailsHandler(DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        super(diagnosticsDetailsUtil);
    }
    @Override
    public DiagnosticDetails handleDiagnosticDetails(Long id, List<DiagnosticDetail> details) {
        final DiagnosticDetail start = getDiagnosticUtil().getDiagnosticParam(id, BOM_TIME);
        final DiagnosticDetail finish = getDiagnosticUtil().getDiagnosticParam(id, EOM_TIME);
        final DiagnosticDetail interfaceD = getDiagnosticUtil().getDiagnosticParam(id, INTERFACE);
        final DiagnosticDetail romTime = getDiagnosticUtil().getDiagnosticParam(id, ROM_TIME);
        final DiagnosticDetail testBytesReceived = getDiagnosticUtil().getDiagnosticParam(id, TEST_BYTES_RECEIVED);
        final DiagnosticDetail totalBytesReceived = getDiagnosticUtil().getDiagnosticParam(id, TOTAL_BYTES_RECEIVED);
        final DiagnosticDetail timeBasedTestMeasurementOffset = getDiagnosticUtil().getDiagnosticParam(id, TIME_BASED_TEST_MEASUREMENT_OFFSET);
        final DiagnosticDetail timeBasedTestMeasurementInterval = getDiagnosticUtil().getDiagnosticParam(id, TIME_BASED_TEST_MEASUREMENT_INTERVAL);
        final DiagnosticDetail timeBasedTestDuration = getDiagnosticUtil().getDiagnosticParam(id, TIME_BASED_TEST_DURATION);
        final DiagnosticDetail numberOfConnections = getDiagnosticUtil().getDiagnosticParam(id, NUMBER_OF_CONNECTIONS);
        details.add(start);
        details.add(finish);
        details.add(interfaceD);

        if (StringUtils.isNotBlank(romTime.getValue())) {
            details.add(romTime);
        }
        if (StringUtils.isNotBlank(timeBasedTestMeasurementInterval.getValue())) {
            details.add(timeBasedTestMeasurementInterval);
        }
        if (StringUtils.isNotBlank(timeBasedTestMeasurementOffset.getValue())) {
            details.add(timeBasedTestMeasurementOffset);
        }
        if (StringUtils.isNotBlank(timeBasedTestDuration.getValue())) {
            details.add(timeBasedTestDuration);
        }
        if (StringUtils.isNotBlank(numberOfConnections.getValue())) {
            details.add(numberOfConnections);
        }
        if (StringUtils.isNotBlank(testBytesReceived.getValue())) {
            details.add(testBytesReceived);
        }
        if (StringUtils.isNotBlank(totalBytesReceived.getValue())) {
            details.add(totalBytesReceived);
        }
        details.add(getDiagnosticUtil().getDiagnosticParam(id, DOWNLOAD_URL));

        DiagnosticsDetailsUtil.calculateDetails(details, start.getValue(), finish.getValue(),
                testBytesReceived.getValue(), totalBytesReceived.getValue(), TransferType.DOWNLOAD);

        return DiagnosticDetails.builder()
                .diagnosticsTypeKey(DOWNLOAD_DIAGNOSTIC.getName())
                .details(details)
                .build();
    }
}
