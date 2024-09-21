package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.reports.ReportType.DEVICE_REGISTRATION;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Component
@RequiredArgsConstructor
public class ExcelDeviceRegistrationReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

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

        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final Integer userDomainId = reportUtils.getUserDomainId(domainId, userId);

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);
        final List<Object[]> deviceRegistrationReports =
                cpeRepository.getFullDeviceRegistrationReport(domainIds, domainIds == null, manufacturer, model, from,
                        to);

        final String path = ReportFileService.createExcelTable(userDomainId, deviceRegistrationReports,
                DEVICE_REGISTRATION, true, session.getClientType(), session.getZoneId(), user.getDateFormat(),
                user.getTimeFormat(), from, to, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return DEVICE_REGISTRATION;
    }
}
