package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.reports.ReportType.DEVICE_ONLINE;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Component
@RequiredArgsConstructor
public class ExcelDeviceOnlineReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserService userService;
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
        final Instant from = params.get("from") == null ? null : Instant.parse((String) params.get("from"));
        final Instant to = params.get("to") == null ? null : Instant.parse((String) params.get("to"));

        final ClientType clientType = session.getClientType();
        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        String zoneId = session.getZoneId();
        final List<Object[]> report = cpeRepository.getDeviceOnlineReport(domainIds, domainIds == null,
                manufacturer, model,
                from != null
                        ? DateTimeUtils.clientToServer(from, clientType, zoneId)
                        : null,
                to != null
                        ? DateTimeUtils.clientToServer(to, clientType, zoneId)
                        : null);

        final Integer userDomainId = reportUtils.getUserDomainId(domainId, session.getUserId());
        final UserResponse user = userService.getUser(session.getUserId(), zoneId);
        final String path = ReportFileService.createExcelTable(userDomainId, report, DEVICE_ONLINE, true,
                clientType, zoneId, user.getDateFormat(), user.getTimeFormat(), from, to, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return DEVICE_ONLINE;
    }
}
