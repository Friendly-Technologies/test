package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.history.orm.acs.repository.DeviceHistoryRepository;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.friendly.commons.models.reports.ReportType.DEVICE_HISTORY;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DATE_IS_NULL;
import static com.friendly.services.infrastructure.utils.DateTimeUtils.addZoneId;

@Component
@RequiredArgsConstructor
public class ExcelDeviceHistoryReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final CpeRepository cpeRepository;
    @NonNull
    private final DeviceHistoryRepository deviceHistoryRepository;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer deviceIdInt = (Integer) params.get("deviceId");
        final Long deviceId = deviceIdInt == null
                ? null
                : Long.valueOf(deviceIdInt);
        final ClientType clientType = session.getClientType();
        final Instant date = params.get("date") == null ? null : Instant.parse((String) params.get("date"));
        if(date == null) {
            throw new FriendlyIllegalArgumentException(DATE_IS_NULL);
        }
        String zoneId = session.getZoneId();
        final Instant from = addZoneId(date, zoneId);
        final Instant to = from.plus(1, ChronoUnit.DAYS);
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, zoneId);
        final Optional<Integer> userDomainId = domainService.getDomainIdByUserId(userId);
        final List<Object[]> deviceHistory = deviceHistoryRepository.getDeviceHistory(deviceId, from, to);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        deviceHistory.forEach(entry -> IntStream.range(0, entry.length).forEach(i -> {
            if (entry[i] instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) entry[i];
                LocalDateTime ld = timestamp.toLocalDateTime();
                entry[i] = formatter.format(ld);
            }
        }));

        final String path = ReportFileService.createExcelTable(userDomainId.orElse(null), deviceHistory,
                DEVICE_HISTORY, true, clientType, zoneId, user.getDateFormat(), user.getTimeFormat(), from, to,
                null, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(clientType, path);
    }

    @Override
    public ReportType getReportType() {
        return DEVICE_HISTORY;
    }
}
