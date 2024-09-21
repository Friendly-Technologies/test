package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.reports.ReportType.FIRMWARE_VERSION;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Component
@RequiredArgsConstructor
public class ExcelFirmwareVersionReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final CpeRepository cpeRepository;
    @NonNull
    private final ReportUtils reportUtils;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer domainId = (Integer) params.get("domainId");
        final String manufacturer = (String) params.get("manufacturer");
        final String model = (String) params.get("model");

        final List<Integer> domainIds = isSuperDomain(domainId)
                        ? null
                        : domainService.getChildDomainIds(domainId);

        final List<Object[]> report =
                cpeRepository.getFirmwareVersionReport(domainIds, domainIds == null, manufacturer, model);

        final Integer userDomainId = reportUtils.getUserDomainId(domainId, session.getUserId());
        final String path = ReportFileService.createExcelTable(userDomainId, report, FIRMWARE_VERSION, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return FIRMWARE_VERSION;
    }
}
