package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.sessions.orm.iotw.repository.SessionRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.sessions.mapper.SessionsMapper;
import com.friendly.services.uiservices.user.UserServiceHelper;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.reports.ReportType.SESSION_STATISTIC;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Component
@RequiredArgsConstructor
public class ExcelSessionStatisticsReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserServiceHelper userServiceHelper;
    @NonNull
    private final SessionRepository sessionRepository;
    @NonNull
    private final SessionsMapper sessionsMapper;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer domainId = (Integer) params.get("domainId");
        final Instant from = params.get("from") == null ? null : Instant.parse((String) params.get("from"));
        final Instant to = params.get("to") == null ? null : Instant.parse((String) params.get("to"));
        final Long userId = session.getUserId();
        final UserEntity user = userServiceHelper.getUser(session.getUserId());
        final Integer userDomainId = isSuperDomain(domainId)
                ? domainService.getDomainIdByUserId(userId).orElse(null)
                : domainId;

        final List<Long> userIds = userServiceHelper.getUserIdsByDomainId(session.getClientType(), domainId);

        final List<Object[]> userSessions =
                sessionsMapper.sessionsToSessionsStatisticObjects(
                        sessionRepository.getFullSessionStatistic(userIds, userIds == null, from, to));

        userSessions.forEach(s -> s[1] = domainService.getDomainNameById((Integer) s[1]));

        final String path =
                ReportFileService.createExcelTable(userDomainId, userSessions, SESSION_STATISTIC, false,
                        session.getClientType(), session.getZoneId(),
                        user.getDateFormat(), user.getTimeFormat(), from, to, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return SESSION_STATISTIC;
    }
}
