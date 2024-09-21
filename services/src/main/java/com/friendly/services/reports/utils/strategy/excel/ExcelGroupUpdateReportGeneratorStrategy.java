package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupRepository;
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

import static com.friendly.commons.models.reports.ReportType.GROUP_UPDATE;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Component
@RequiredArgsConstructor
public class ExcelGroupUpdateReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final UpdateGroupRepository updateGroupRepository;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final ClientType clientType = session.getClientType();
        final Integer domainId = (Integer) params.get("domainId");
        final String manufacturer = (String) params.get("manufacturer");
        final String model = (String) params.get("model");
        String zoneId = session.getZoneId();
        final Instant from = params.get("from") == null ? null : DateTimeUtils.clientToServer(
                Instant.parse((String) params.get("from")), clientType, zoneId);
        final Instant to = params.get("to") == null ? null : DateTimeUtils.clientToServer(
                Instant.parse((String) params.get("to")), clientType, zoneId);

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        final List<Object[]> report =
                updateGroupRepository.findAll(domainIds, domainIds == null, manufacturer, model, from, to);

        final Integer userDomainId = isSuperDomain(domainId)
                ? domainService.getDomainIdByUserId(session.getUserId()).orElse(null)
                : domainId;
        final UserResponse user = userService.getUser(session.getUserId(), zoneId);
        final String path = ReportFileService.createExcelTable(userDomainId, report, GROUP_UPDATE, true,
                clientType, zoneId, user.getDateFormat(), user.getTimeFormat(), from, to, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return GROUP_UPDATE;
    }
}
