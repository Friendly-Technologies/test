package com.friendly.services.reports.utils;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.DeviceActivityType;
import com.friendly.commons.models.reports.DeviceActivityTypeDescription;
import com.friendly.commons.models.reports.DeviceDistributionReport;
import com.friendly.commons.models.reports.DeviceReport;
import com.friendly.commons.models.reports.DistributionItem;
import com.friendly.commons.models.reports.OperationsReport;
import com.friendly.commons.models.reports.UserActivityReport;
import com.friendly.commons.models.reports.UserActivityType;
import com.friendly.commons.models.reports.UserActivityTypeDescription;
import com.friendly.commons.models.settings.DatabaseType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.device.info.orm.acs.model.projections.UserOperationsProjection;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.uiservices.statistic.orm.iotw.model.DeviceLogEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.uiservices.statistic.orm.iotw.model.UserLogEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.DeviceLogRepository;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.utils.CommonUtils.SUPER_DOMAIN_NAME;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportUtils {

    @NonNull
    private final CpeRepository cpeRepository;
    @NonNull
    private final UserService userService;
    @NonNull
    private final DomainService domainService;
    @NonNull
    private final DeviceLogRepository deviceLogRepository;

    private static final Map<String, ReportStatus> reportStatusMap = new ConcurrentHashMap<>();
    private static final List<UserActivityTypeDescription> USER_ACTIVITY_TYPES = new ArrayList<>();
    private static final List<DeviceActivityTypeDescription> DEVICE_UPDATE_ACTIVITY_TYPES = new ArrayList<>();
    private static final String DATE_FORMATTER = "dd/MM/yyyy HH";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");

    @PostConstruct
    public void init() {
        fillUserActivityType();
        fillDeviceUpdateActivityTypes();
    }

    public static String getFileName(String name, String username) {
        String formattedDate = LocalDateTime.now().format(FORMATTER);
        return new StringBuilder("Report")
                .append("(Inventory_")
                .append("[")
                .append(name)
                .append("]_")
                .append(formattedDate)
                .append(")")
                .append("'SP'")
                .append(username)
                .append(".xlsx")
                .toString();
    }

    public static void addReportStatus(String fileName, ReportStatus status) {
        reportStatusMap.put(fileName, status);
    }

    public static ReportStatus getReportStatus(String fileName) {
        return reportStatusMap.get(fileName);
    }

    public static void removeReportStatus(String fileName) {
        reportStatusMap.remove(fileName);
    }

    public static List<UserActivityTypeDescription> getUserActivityTypes() {
        return USER_ACTIVITY_TYPES.stream()
                .sorted(Comparator.comparing(UserActivityTypeDescription::getActivityName))
                .collect(Collectors.toList());
    }

    public static List<DeviceActivityTypeDescription> getDeviceUpdateActivityTypes() {
        return DEVICE_UPDATE_ACTIVITY_TYPES;
    }

    private void fillUserActivityType() {
        for (UserActivityType activityType : UserActivityType.values()) {
            USER_ACTIVITY_TYPES.add(new UserActivityTypeDescription(activityType, activityType.getDescription()));
        }
    }

    private void fillDeviceUpdateActivityTypes() {
        for (DeviceActivityType activityType : DeviceActivityType.values()) {
            DEVICE_UPDATE_ACTIVITY_TYPES.add(new DeviceActivityTypeDescription(activityType, activityType.getDescription()));
        }
        DEVICE_UPDATE_ACTIVITY_TYPES.sort(Comparator.comparing(DeviceActivityTypeDescription::getActivityName));
    }

    public List<DeviceReport> deviceLogToDeviceReport(final List<DeviceLogEntity> entities,
            final List<ProductClassGroupEntity> productClassGroupEntityList, final String zoneId,
            final String dateFormat, final String timeFormat) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(e -> {
                    ProductClassGroupEntity productClassGroup = productClassGroupEntityList.stream()
                            .filter(p -> p.getId().equals(e.getGroupId()))
                            .findFirst()
                            .orElse(null);

                    return deviceLogsToDeviceReports(e, productClassGroup, zoneId, dateFormat, timeFormat);
                })
                .collect(Collectors.toList());
    }

    public DeviceReport deviceLogsToDeviceReports(final DeviceLogEntity entity,
            final ProductClassGroupEntity productClassGroupEntity, final String zoneId, final String dateFormat,
            final String timeFormat) {
        final UserResponse user = userService.getUser(entity.getUserId(), zoneId);
        String manufacturer = productClassGroupEntity == null ? "" : productClassGroupEntity.getManufacturerName();
        String model = productClassGroupEntity == null ? "" : productClassGroupEntity.getModel();
        String serial = entity.getSerial() == null ? "" : entity.getSerial();

        return DeviceReport.builder()
                .activityType(entity.getActivityType())
                .activityName(entity.getActivityType().getDescription())
                .dateIso(entity.getDate())
                .date(DateTimeUtils.format(entity.getDate(), zoneId, dateFormat, timeFormat))
                .userName(user.getUsername())
                .domain(user.getDomain().getName())
                .serial(serial)
                .manufacturer(manufacturer)
                .model(model)
                .note(entity.getNote() == null
                        || entity.getNote().isEmpty()
                        || entity.getNote().toLowerCase().contains("no value")
                        ? ""
                        : entity.getNote())
                .build();
    }

    public List<UserActivityReport> userLogsToUserReports(final List<UserLogEntity> entities, final String zoneId,
                                                           final String dateFormat, final String timeFormat) {
        return entities.stream()
                .map(e -> userLogToUserReport(e, zoneId, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    public UserActivityReport userLogToUserReport(final UserLogEntity entity, final String zoneId,
                                                   final String dateFormat, final String timeFormat) {
        final UserResponse user = userService.getUserByIdWithoutDomain(entity.getUserId(), zoneId);
        final String domainName = domainService.getDomainNameById(user.getDomainId());
        String username;

        if (SUPER_DOMAIN_NAME.equals(domainName) || domainName == null || domainName.isEmpty()) {
            username = user.getUsername();
        } else {
            username = String.format("%s@%s", user.getUsername(), domainName);
        }


        return UserActivityReport.builder()
                .userName(username)
                .activityType(entity.getActivityType())
                .activityName(entity.getActivityType().getDescription())
                .dateIso(entity.getDate())
                .date(DateTimeUtils.format(entity.getDate(), zoneId, dateFormat, timeFormat))
                .note(entity.getNote() == null
                        || entity.getNote().isEmpty()
                        || entity.getNote().toLowerCase().contains("no value")
                        ? ""
                        : entity.getNote())
                .build();
    }

    public List<OperationsReport> getOperationsReportsByUserAndDomain(final DeviceActivityType activityType,
                                                                       final Instant from, final Instant to,
                                                                       final Session session, final UserResponse user,
                                                                       final List<UserEntity> userEntityList) {
        final ClientType clientType = session.getClientType();
        final String zoneId = session.getZoneId();
        final List<UserOperationsProjection> operationsMap;
        List<Long> userIds = userEntityList.stream().map(UserEntity::getId).collect(Collectors.toList());
        if (DbConfig.getDbType().equals(DatabaseType.Oracle)) {
            operationsMap = deviceLogRepository.findByUserIdOracle(clientType, activityType, from, to, userIds);
        } else {
            operationsMap = deviceLogRepository.findByUserIdMySql(clientType == ClientType.sc ? 0 : 1,
                    activityType != null ? activityType.name() : null,
                    from, to, userIds);
        }

        List<OperationsReport> operationsReportList = operationsMap.stream()
                .map(m -> {
                    final Instant date = getInstantFromString(m.getDateOp());
                    final Integer operations = m.getOperations().intValue();
                    Integer domainId = userEntityList.stream()
                            .filter(e -> e.getId().equals(m.getUserId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("User not found"))
                            .getDomainId();
                    return OperationsReport.builder()
                            .count(operations)
                            .date(DateTimeUtils.format(date, zoneId, user.getDateFormat(), user.getTimeFormat()))
                            .dateIso(DateTimeUtils.addZoneId(date, zoneId))
                            .domainId(domainId)
                            .build();
                })
                .collect(Collectors.toList());

        return fillMissingHours(operationsReportList, from, to, user, zoneId);
    }

    public static List<OperationsReport> fillMissingHours(final List<OperationsReport> operations, final Instant from,
                                                          final Instant to, final UserResponse user,
                                                          final String zoneId) {
        Map<String, OperationsReport> operationsMap = operations.stream()
                .collect(Collectors.toMap(OperationsReport::getDate, Function.identity(), (existing, replacement) -> replacement));

        List<OperationsReport> operationsWithEmptyHours = new ArrayList<>();

        for (Instant current = from; !current.isAfter(to); current = current.plusSeconds(3600)) {
            String currentStr = DateTimeUtils.format(current, zoneId, user.getDateFormat(), user.getTimeFormat());

            OperationsReport operation = operationsMap.get(currentStr);
            if (operation == null) {
                operationsWithEmptyHours.add(createEmptyOperationsReport(currentStr, current));
            } else {
                operationsWithEmptyHours.add(operation);
            }
        }

        return operationsWithEmptyHours;
    }

    private static OperationsReport createEmptyOperationsReport(String date, Instant dateIso) {
        return OperationsReport.builder()
                .dateIso(dateIso)
                .date(date)
                .count(0)
                .domainId(0)
                .build();
    }

    private Instant getInstantFromString(final String date) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER)
                .withZone(ZoneId.of("UTC"));
        return Instant.from(formatter.parse(date));
    }

    public DeviceDistributionReport getDeviceDistributionReport(final Integer domainId,
                                                                final String manufacturer,
                                                                final Instant from,
                                                                final Instant to) {

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        final List<DistributionItem> items =
                cpeRepository.getDeviceDistributionReport(domainIds, manufacturer, from, to)
                        .stream()
                        .map(r -> DistributionItem.builder()
                                .domain(r.getDomainName())
                                .manufacturer(r.getManufacturerName())
                                .model(r.getModel())
                                .quantity(r.getCount())
                                .build())
                        .collect(Collectors.toList());

        final int totalQuantity = items.stream().mapToInt(DistributionItem::getQuantity).sum();

        return DeviceDistributionReport.builder()
                .totalQuantity(totalQuantity)
                .items(items.stream()
                        .map(item -> item.toBuilder()
                                .percentage(getPercentage(totalQuantity,
                                        item.getQuantity()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private BigDecimal getPercentage(final int totalQuantity, final Integer quantity) {
        return BigDecimal.valueOf(quantity)
                .divide(BigDecimal.valueOf(totalQuantity), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public List<Long> getUserIds(final Integer domainId, final Long userId, final ClientType clientType) {
        return userId != null
                ? Collections.singletonList(userId)
                : userService.getUserIdsByClientTypeAndDomainIds(clientType,
                domainService.getChildDomainIds(domainId));
    }

    public String getClientType (ClientType clientType) {
        return clientType == ClientType.sc ? "SP" : "MP";
    }

    public Integer getUserDomainId(final Integer domainId, final Long userId) {
        return isSuperDomain(domainId)
                ? domainService.getDomainIdByUserId(userId).orElse(null)
                : domainId;
    }

    public String getUsername(final Long userId, final String zoneId) {
        return userService.getUserByIdWithoutDomain(userId, zoneId).getUsername();
    }

    public List<UserActivityReport> objectArrayToUserReports(List<Object[]> objectArrayList, String zoneIdString) {
        List<UserActivityReport> reports = new ArrayList<>();

        ZoneId zoneId = ZoneId.of(zoneIdString);

        for (Object[] row : objectArrayList) {
            String userName = (String) row[0];
            UserActivityType activityType = (UserActivityType) row[1];
            Instant dateInstant = (Instant) row[2];
            String note = (String) row[3];

            LocalDateTime date = LocalDateTime.ofInstant(dateInstant, zoneId);

            UserActivityReport report = new UserActivityReport();
            report.setUserName(userName);
            report.setActivityType(activityType);
            report.setActivityName(activityType.name());

            String formattedDate = date.toString();
            report.setDate(formattedDate);

            report.setDateIso(dateInstant);

            report.setNote(note);

            reports.add(report);
        }

        return reports;
    }
}
