package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.reports.UserActivityType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.uiservices.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.reports.ReportType.USER_ACTIVITY_SYSTEM;

@Component
@RequiredArgsConstructor
public class ExcelUserActivitySystemReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @PersistenceContext
    private EntityManager entityManager;
    private final ReportUtils reportUtils;
    private final UserService userService;
    private static final String SLASH = "/";
    private final ReportFileService reportFileService;
    private static final Map<String, Integer> paramMap = new HashMap<>();

    @PostConstruct
    public void init() {
        paramMap.put("Username",0);
        paramMap.put("Activity type",1);
        paramMap.put("Date",2);
        paramMap.put("Note",3);
    }

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer domainId = (Integer) params.get("domainId");
        final ClientType clientType = session.getClientType();
        final String activityTypeStr = (String) params.get("activityType");
        final Integer userIdInt = (Integer) params.get("userId");
        final Long userId = userIdInt == null ? null : Long.valueOf(userIdInt);
        UserActivityType activityType = activityTypeStr != null ? UserActivityType.byName(activityTypeStr) : null;

        Instant from = params.get("from") != null ? Instant.parse((String) params.get("from")) : null;
        Instant to = params.get("to") != null ? Instant.parse((String) params.get("to")) : null;

        final String path = domainId + SLASH + fileName;

        TypedQuery<Object[]> query;
        if (userId == null) {
            query = entityManager.createQuery("SELECT u.username, l.activityType, l.date, l.note " +
                    "FROM UserLogEntity l " +
                    "JOIN UserEntity u ON u.id = l.userId " +
                    "WHERE l.clientType = :clientType " +
                    "AND (:activityType is null or l.activityType = :activityType) " +
                    "AND (:from is null or l.date >= :from) " +
                    "AND (:to is null or l.date <= :to)", Object[].class);
            query.setParameter("clientType", clientType);
            query.setParameter("activityType", activityType);
            query.setParameter("from", from);
            query.setParameter("to", to);
        }
        else {
            final List<Long> userIds = reportUtils.getUserIds(domainId, userId, clientType);
            query = entityManager.createQuery("SELECT u.username, l.activityType, l.date, l.note " +
                    "FROM UserLogEntity l " +
                    "JOIN UserEntity u ON u.id = l.userId " +
                    "WHERE l.clientType = :clientType " +
                    "AND (:activityType is null or l.activityType = :activityType) " +
                    "AND (:from is null or l.date >= :from) " +
                    "AND (:to is null or l.date <= :to) " +
                    "AND l.userId IN :userIds ", Object[].class);
            query.setParameter("clientType", clientType);
            query.setParameter("activityType", activityType);
            query.setParameter("from", from);
            query.setParameter("to", to);
            query.setParameter("userIds", userIds);
        }

        reportFileService.createExcelFile(query, path, clientType, paramMap, "User Activity System Sheet");
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
    }

    @Override
    public ReportType getReportType() {
        return USER_ACTIVITY_SYSTEM;
    }
}
