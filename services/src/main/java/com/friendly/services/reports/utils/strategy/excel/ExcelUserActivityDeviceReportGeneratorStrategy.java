package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.DeviceActivityType;
import com.friendly.commons.models.reports.DeviceReport;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.service.DeviceService;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.uiservices.statistic.orm.iotw.model.DeviceLogEntity;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.DeviceLogRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.ReportType.USER_ACTIVITY_DEVICE;

@Component
@RequiredArgsConstructor
public class ExcelUserActivityDeviceReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DeviceService deviceService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final DeviceLogRepository deviceLogRepository;
    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;
    @NonNull
    private final ReportUtils reportUtils;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer domainId = (Integer) params.get("domainId");
        final Integer userIdInt = (Integer) params.get("userId");
        final Long userId = userIdInt == null ? null : Long.valueOf(userIdInt);
        String activityTypeStr = (String) params.get("activityType");
        final DeviceActivityType activityType = StringUtils.isEmpty(activityTypeStr)
                ? null
                : DeviceActivityType.byName(activityTypeStr);
        final String serial = (String) params.get("serial");
        final Instant from = params.get("from") == null
                ? null
                : Instant.parse((String) params.get("from"));
        final Instant to = params.get("to") == null
                ? null
                : Instant.parse((String) params.get("to"));
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final List<Long> userIds = reportUtils.getUserIds(domainId, userId, clientType);
        final Long deviceId = serial == null
                ? null
                : deviceService.getDeviceIdBySerial(serial);

        final List<DeviceLogEntity> deviceLogs = deviceLogRepository.findAll(clientType, activityType, from, to,
                userIds, deviceId, serial);
        Set<Long> groupIdList = deviceLogs.stream()
                .map(DeviceLogEntity::getGroupId)
                .collect(Collectors.toSet());
        final List<ProductClassGroupEntity> productClassGroupEntityList =
                productClassGroupRepository.findAllByIdIn(groupIdList);

        List<DeviceReport> deviceReports = reportUtils.deviceLogToDeviceReport(deviceLogs, productClassGroupEntityList,
                session.getZoneId(), user.getDateFormat(), user.getTimeFormat());

        final List<Object[]> deviceUpdateReports = deviceReports.stream()
                .map(r -> new Object[]{r.getUserName(), r.getManufacturer(), r.getModel(), r.getSerial(), r.getDomain(),
                        r.getDateIso(), r.getActivityType(), r.getNote()}).collect(Collectors.toList());

        final Integer userDomainId = reportUtils.getUserDomainId(domainId, userId);
        final String path = ReportFileService.createExcelTable(userDomainId, deviceUpdateReports,
                USER_ACTIVITY_DEVICE, false, session.getClientType(), session.getZoneId(), user.getDateFormat(),
                user.getTimeFormat(), from, to, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return USER_ACTIVITY_DEVICE;
    }
}
