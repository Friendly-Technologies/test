package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.reports.DeviceActivityType;
import com.friendly.commons.models.reports.OperationsReport;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.device.info.orm.acs.repository.DomainRepository;
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
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.ReportType.STATISTIC_OPERATIONS;
import static com.friendly.services.infrastructure.utils.CommonUtils.SUPER_DOMAIN_NAME;

@Component
@RequiredArgsConstructor
public class ExcelStatisticOperationsReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final DomainRepository domainRepository;
    @NonNull
    private final ReportUtils reportUtils;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer domainId = (Integer) params.get("domainId");
        String activityTypeStr = (String) params.get("activityType");
        final DeviceActivityType activityType = StringUtils.isEmpty(activityTypeStr)
                ? null : DeviceActivityType.byName(activityTypeStr);
        final Instant from = params.get("from") == null ? null : Instant.parse((String) params.get("from"));
        final Instant to = params.get("to") == null ? null : Instant.parse((String) params.get("to"));
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final Integer userDomainId = reportUtils.getUserDomainId(domainId, session.getUserId());
        final List<UserEntity> userEntityList =
                userService.getUserEntitiesByDomain(session.getClientType(), domainService.getChildDomainIds(domainId));
        final List<OperationsReport> operations =
                reportUtils.getOperationsReportsByUserAndDomain(activityType, from, to, session, user, userEntityList);
        String domainName; if (domainId != 0) {
            domainName = domainRepository.getDomains(domainId).get(0).getName();
        } else {
            domainName = SUPER_DOMAIN_NAME;
        } final List<Object[]> operationsReport =
                operations.stream().map(r -> new Object[]{domainName, r.getDateIso(), r.getCount()})
                        .collect(Collectors.toList());

        final String path =
                ReportFileService.createExcelTable(userDomainId, operationsReport, STATISTIC_OPERATIONS,
                        false, session.getClientType(), session.getZoneId(), user.getDateFormat(), user.getTimeFormat(),
                        from, to, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return STATISTIC_OPERATIONS;
    }
}
