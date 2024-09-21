package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.device.activity.orm.acs.repository.TaskRepository;
import com.friendly.services.device.activity.orm.acs.repository.TransactionRepository;
import com.friendly.services.settings.sessions.orm.iotw.repository.SessionRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserServiceHelper;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.ReportType.ACTIVE_SESSIONS;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Component
@RequiredArgsConstructor
public class ExcelActiveSessionsReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserServiceHelper userServiceHelper;
    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final TransactionRepository transactionRepository;
    @NonNull
    private final SessionRepository sessionRepository;
    @NonNull
    private final ReportUtils reportUtils;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer domainId = (Integer) params.get("domainId");
        final Long userId = session.getUserId();
        final UserEntity user = userServiceHelper.getUser(session.getUserId());
        final Integer userDomainId = isSuperDomain(domainId)
                ? domainService.getDomainIdByUserId(userId).orElse(null)
                : domainId;

        final List<Long> userIds = userServiceHelper.getUserIdsByDomainId(session.getClientType(), domainId);
        final List<Object[]> sessionEntityList =
                sessionRepository.getFullActiveSessions(userIds, userIds == null, Instant.now());
        List<Object[]> list = sessionEntityList.stream().map(s -> {
            String name = reportUtils.getClientType(session.getClientType()) + "/" + user.getName();
            final List<Integer> transactionIds =
                    transactionRepository.getTransactionIdsInRange(
                            name, (Instant) s[3], (Instant) s[2]);
            final Integer count = taskRepository.getCpeIdsFromTransactionIds(transactionIds);
            return new Object[]{s[0], domainService.getDomainNameById((Integer) s[1]), s[2], s[3], count};
        }).collect(Collectors.toList());

        final String path = ReportFileService.createExcelTable(userDomainId, list, ACTIVE_SESSIONS,
                false, session.getClientType(), session.getZoneId(), user.getDateFormat(), user.getTimeFormat(), null,
                null, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return ACTIVE_SESSIONS;
    }
}
