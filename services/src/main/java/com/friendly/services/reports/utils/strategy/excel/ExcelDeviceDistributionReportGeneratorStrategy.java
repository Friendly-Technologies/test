package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.reports.DeviceDistributionReport;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import com.friendly.services.reports.utils.ReportUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.ReportType.DEVICE_DISTRIBUTION;

@Component
@RequiredArgsConstructor
public class ExcelDeviceDistributionReportGeneratorStrategy implements ExcelReportGeneratorStrategy {
    @NonNull
    private final ReportUtils reportUtils;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer domainId = (Integer) params.get("domainId");
        final String manufacturer = (String) params.get("manufacturer");
        final Instant from = params.get("from") == null ? null : Instant.parse((String) params.get("from"));
        final Instant to = params.get("to") == null ? null : Instant.parse((String) params.get("to"));

        final DeviceDistributionReport deviceDistributionReport = reportUtils.getDeviceDistributionReport(domainId,
                manufacturer, from, to);

        final List<Object[]> deviceUpdateReports = deviceDistributionReport.getItems().stream()
                .map(r -> new Object[]{r.getDomain(), r.getManufacturer(), r.getModel(), r.getQuantity(),
                        r.getPercentage()})
                .collect(Collectors.toList());

        final Integer userDomainId = reportUtils.getUserDomainId(domainId, session.getUserId());
        final String path = ReportFileService.createExcelTable(userDomainId, deviceUpdateReports,
                DEVICE_DISTRIBUTION, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return DEVICE_DISTRIBUTION;
    }
}
