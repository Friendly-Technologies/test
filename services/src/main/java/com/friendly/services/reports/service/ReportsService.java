package com.friendly.services.reports.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.FTPageDetails;
import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.DeviceDistributionBody;
import com.friendly.commons.models.reports.DeviceDistributionReport;
import com.friendly.commons.models.reports.DeviceEventBody;
import com.friendly.commons.models.reports.DeviceEventItem;
import com.friendly.commons.models.reports.DeviceEventReport;
import com.friendly.commons.models.reports.DeviceOfflineBody;
import com.friendly.commons.models.reports.DeviceOfflineItem;
import com.friendly.commons.models.reports.DeviceOfflineReport;
import com.friendly.commons.models.reports.DeviceOnlineBody;
import com.friendly.commons.models.reports.DeviceOnlineItem;
import com.friendly.commons.models.reports.DeviceOnlineReport;
import com.friendly.commons.models.reports.DeviceRegistrationReport;
import com.friendly.commons.models.reports.DeviceRegistrationReportBody;
import com.friendly.commons.models.reports.DeviceReport;
import com.friendly.commons.models.reports.DeviceUpdateReportBody;
import com.friendly.commons.models.reports.FileDates;
import com.friendly.commons.models.reports.FileDatesList;
import com.friendly.commons.models.reports.FileReport;
import com.friendly.commons.models.reports.FirmwareVersionBody;
import com.friendly.commons.models.reports.FirmwareVersionReport;
import com.friendly.commons.models.reports.GeneratedReportsBody;
import com.friendly.commons.models.reports.OnlineItem;
import com.friendly.commons.models.reports.OperationsReport;
import com.friendly.commons.models.reports.ProfileDownloadBody;
import com.friendly.commons.models.reports.ProfileDownloadReport;
import com.friendly.commons.models.reports.ReportFileBody;
import com.friendly.commons.models.reports.ReportFormat;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.reports.StatisticOperationsBody;
import com.friendly.commons.models.reports.StatisticOperationsReport;
import com.friendly.commons.models.reports.UserActivityReport;
import com.friendly.commons.models.reports.UserActivityReportBody;
import com.friendly.commons.models.reports.response.DeviceActivityTypeDescriptionsResponse;
import com.friendly.commons.models.reports.response.UserActivityTypeDescriptionsResponse;
import com.friendly.commons.models.settings.DatabaseType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.service.DeviceService;
import com.friendly.services.reports.mapper.ReportMapper;
import com.friendly.services.reports.dto.ReportResponse;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.device.info.orm.acs.model.projections.CpeRegReportProjection;
import com.friendly.services.productclass.orm.acs.model.projections.ProductGroupFirmwareReportProjection;
import com.friendly.services.management.profiles.orm.acs.model.ProfileDownloadReportProjection;
import com.friendly.services.uiservices.statistic.orm.iotw.model.DeviceLogEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.uiservices.statistic.orm.iotw.model.UserLogEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.management.profiles.orm.acs.repository.ProfileFileRepository;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.DeviceLogRepository;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.UserLogRepository;
import com.friendly.services.reports.utils.strategy.ReportStrategy;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.friendly.services.uiservices.view.ViewService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FILE_FORMAT_NOT_SUPPORTED;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

/**
 * Service that exposes the base functionality for Reports
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportsService {
    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final ReportMapper reportMapper;
    @NonNull
    private final CpeRepository cpeRepository;
    @NonNull
    private final UserLogRepository userLogRepository;
    @NonNull
    private final DeviceLogRepository deviceLogRepository;
    @NonNull
    private final ProfileFileRepository profileFileRepository;
    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;
    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final DeviceService deviceService;
    @NonNull
    private final ViewService viewService;
    @NonNull
    private final ReportStrategy excelReportStrategy;
    @NonNull
    private final ReportStrategy xmlReportStrategy;
    @NonNull
    private final ReportStrategy csvReportStrategy;
    @NonNull
    private final WsSender wsSender;
    @NonNull
    private final ReportUtils reportUtils;
    @PersistenceContext
    private EntityManager entityManager;

    private static final Map<String, Comparator<FileReport>> COMPARATOR_MAP = new HashMap<>();
    private static final Map<ReportFormat, ReportStrategy> REPORT_STRATEGY_MAP = new EnumMap<>(ReportFormat.class);

    @PostConstruct
    public void init() {
        REPORT_STRATEGY_MAP.put(ReportFormat.EXCEL, excelReportStrategy);
        REPORT_STRATEGY_MAP.put(ReportFormat.XML, xmlReportStrategy);
        REPORT_STRATEGY_MAP.put(ReportFormat.CSV, csvReportStrategy);

        COMPARATOR_MAP.put(null, Comparator.nullsLast(Comparator.comparing(
                FileReport::getCreatedIso, Comparator.nullsLast(Comparator.naturalOrder()))));
        COMPARATOR_MAP.put("name", Comparator.nullsLast(Comparator.comparing(
                FileReport::getName, Comparator.nullsLast(Comparator.naturalOrder()))));
        COMPARATOR_MAP.put("domain", Comparator.nullsLast(Comparator.comparing(
                FileReport::getDomain, Comparator.nullsLast(Comparator.naturalOrder()))));
        COMPARATOR_MAP.put("type", Comparator.nullsLast(Comparator.comparing(
                FileReport::getType, Comparator.nullsLast(Comparator.naturalOrder()))));
        COMPARATOR_MAP.put("created", Comparator.nullsLast(Comparator.comparing(
                FileReport::getCreatedIso, Comparator.nullsLast(Comparator.naturalOrder()))));
        COMPARATOR_MAP.put("size", Comparator.nullsLast(Comparator.comparing(
                FileReport::getSize, Comparator.nullsLast(Comparator.naturalOrder()))));
        COMPARATOR_MAP.put("updated", Comparator.nullsLast(Comparator.comparing(
                FileReport::getUpdated, Comparator.nullsLast(Comparator.naturalOrder()))));
        COMPARATOR_MAP.put("creator", Comparator.nullsLast(Comparator.comparing(
                FileReport::getCreator, Comparator.nullsLast(Comparator.naturalOrder()))));
    }

    public String generateReport(final String token, final ReportFileBody body) {
        ReportStrategy strategy = REPORT_STRATEGY_MAP.get(body.getReportFormat());
        if (strategy == null) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
        Map<String, Object> params = body.getParams();
        Session session = jwtService.getSession(token);
        ReportType reportType = body.getReportType();
        final String name = Optional.ofNullable((Integer) params.get("viewId"))
                .map(Long::valueOf)
                .map(id -> viewService.getAbstractView(id, session).getName())
                .orElseGet(reportType::name);

        final Long userId = session.getUserId();
        String zoneId = session.getZoneId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, zoneId);
        String fileName = ReportUtils.getFileName(name, user.getUsername());
        ReportUtils.addReportStatus(fileName, ReportStatus.IN_PROGRESS);
        strategy.generateReport(reportType, session, params, fileName);
        return fileName;
    }

    public List<ReportResponse> getReportStatuses(List<String> fileNames) {
        return fileNames.stream()
                .map(fileName -> {
                    ReportStatus status = ReportUtils.getReportStatus(fileName);
                    if (status == null) {
                        status = ReportStatus.NOT_FOUND;
                    } else if (status == ReportStatus.COMPLETED) {
                        ReportUtils.removeReportStatus(fileName);
                    }
                    return new ReportResponse(fileName, status);
                })
                .collect(Collectors.toList());
    }


    public FTPage<DeviceRegistrationReport> getDeviceRegistrationReport(final String token,
                                                                        final DeviceRegistrationReportBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "created");
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final Integer domainId = body.getDomainId();

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);
        final List<Page<CpeRegReportProjection>> reportsPageList =
                pageable.stream()
                        .map(p -> cpeRepository.getDeviceRegistrationReport(domainIds, domainIds == null, body.getManufacturer(),
                                body.getModel(), body.getFrom(), body.getTo(), p))
                        .collect(Collectors.toList());
        final FTPage<DeviceRegistrationReport> userPage = new FTPage<>();

        return userPage.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(reportsPageList))
                .items(reportsPageList.stream()
                        .map(Page::getContent)
                        .flatMap(c -> reportMapper
                                .arrayToDeviceRegistrationReports(c, session.getClientType(),
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList()))
                .build();
    }

    public FTPage<DeviceReport> getDeviceUpdateReport(final String token, final DeviceUpdateReportBody body) {
        final List<Pageable> pageable =
                PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(), body.getSorts(), "id");
        final String serial = body.getSerial();
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final List<Long> userIds = reportUtils.getUserIds(body.getDomainId(), body.getUserId(), clientType);
        final Long deviceId = serial == null ? null : deviceService.getDeviceIdBySerial(serial);
        final List<Page<DeviceLogEntity>> deviceLogs = pageable.stream()
                .map(p -> deviceLogRepository.findAll(clientType, body.getActivityType(), body.getFrom(), body.getTo(),
                        userIds, deviceId, serial, p))
                .collect(Collectors.toList());
        Set<Long> groupIdList = deviceLogs.stream()
                .flatMap(p -> p.getContent().stream())
                .map(DeviceLogEntity::getGroupId)
                .collect(Collectors.toSet());
        final List<ProductClassGroupEntity> productClassGroupEntityList =
                productClassGroupRepository.findAllByIdIn(groupIdList);
        final FTPage<DeviceReport> devicePage = new FTPage<>();

        return devicePage.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(deviceLogs))
                .items(deviceLogs.stream()
                        .map(Page::getContent)
                        .flatMap(c -> reportUtils.deviceLogToDeviceReport(c, productClassGroupEntityList,
                                session.getZoneId(), user.getDateFormat(), user.getTimeFormat()).stream())
                        .collect(Collectors.toList()))
                .build();
    }

    public FTPage<UserActivityReport> getUserActivityReport(final String token, final UserActivityReportBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final ClientType clientType = session.getClientType();
        final List<Long> userIds = reportUtils.getUserIds(body.getDomainId(), body.getUserId(), clientType);

        if (body.getPageNumbers() == null && body.getPageSize() == null) {
            TypedQuery<Object[]> query = entityManager.createQuery("SELECT u.username, l.activityType, l.date, l.note " +
                    "FROM UserLogEntity l " +
                    "JOIN UserEntity u ON u.id = l.userId " +
                    "WHERE l.clientType = :clientType " +
                    "AND (:activityType is null or l.activityType = :activityType) " +
                    "AND (:from is null or l.date >= :from) " +
                    "AND (:to is null or l.date <= :to)", Object[].class);
            query.setParameter("clientType", clientType);
            query.setParameter("activityType", body.getActivityType());
            query.setParameter("from", body.getFrom());
            query.setParameter("to", body.getTo());

            try {
                return processReportForPrint(query, session);
            }
            catch (InterruptedException | IOException e ) {
                throw new RuntimeException(e);
            }
        }
        else {
            final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(), body.getSorts(), "date");

            List<Page<UserLogEntity>> deviceLogs = pageable.stream()
                    .map(p -> userLogRepository.findAll(clientType, body.getActivityType(), body.getFrom(), body.getTo(), userIds, p))
                    .collect(Collectors.toList());

            final FTPage<UserActivityReport> devicePage = new FTPage<>();
            return devicePage.toBuilder()
                    .pageDetails(PageUtils.buildPageDetails(deviceLogs))
                    .items(deviceLogs.stream()
                            .map(Page::getContent)
                            .flatMap(r -> reportUtils.userLogsToUserReports(r, session.getZoneId(),
                                            user.getDateFormat(),
                                            user.getTimeFormat())
                                    .stream())
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    public FTPage<UserActivityReport> processReportForPrint(TypedQuery<Object[]> query, Session session) throws IOException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Object[]> values = query.getResultList();

        List<Future<List<UserActivityReport>>> futures = new ArrayList<>();
        int batchSize = 1000;

        for (int i = 0; i < values.size(); i += batchSize) {
            int fromIndex = i;
            int toIndex = Math.min(i + batchSize, values.size());

            Future<List<UserActivityReport>> future = executorService.submit(() -> {
                List<Object[]> batch = values.subList(fromIndex, toIndex);
                return reportUtils.objectArrayToUserReports(batch, session.getZoneId());
            });

            futures.add(future);
        }

        List<UserActivityReport> reports = new ArrayList<>();
        for (Future<List<UserActivityReport>> future : futures) {
            try {
                reports.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Error processing report for print", e);
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        final FTPage<UserActivityReport> devicePage = new FTPage<>();
        return devicePage.toBuilder()
                .pageDetails(FTPageDetails.builder()
                        .totalPages(1)
                        .totalItems((long) reports.size())
                        .pageItems(reports.size())
                        .build())
                .items(reports)
                .build();
    }



    public StatisticOperationsReport getStatisticOperationsReport(final String token,
                                                                  final StatisticOperationsBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final List<UserEntity> userEntityList = userService.getUserEntitiesByDomain(session.getClientType(),
                domainService.getChildDomainIds(body.getDomainId()));
        final List<OperationsReport> operations = reportUtils.getOperationsReportsByUserAndDomain(body.getActivityType(),
                body.getFrom(), body.getTo(), session, user, userEntityList);

        return StatisticOperationsReport.builder()
                .operations(operations)
                .maxValue(operations.stream()
                        .map(OperationsReport::getCount)
                        .max(Integer::compareTo)
                        .orElse(0))
                .build();
    }

    public DeviceDistributionReport getDeviceDistributionReport(final String token,
                                                                final DeviceDistributionBody body) {
        jwtService.getSession(token);
        return reportUtils.getDeviceDistributionReport(body.getDomainId(), body.getManufacturer(), body.getFrom(), body.getTo());
    }

    public DeviceOnlineReport getDeviceOnlineReport(final String token, final DeviceOnlineBody body) {
        final Session session = jwtService.getSession(token);
        final Integer domainId = body.getDomainId();
        final String manufacturer = body.getManufacturer();
        final String model = body.getModel();
        final Instant from = body.getFrom();
        final Instant to = body.getTo();

        final ClientType clientType = session.getClientType();
        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        final List<OnlineItem> items;
        String zoneId = session.getZoneId();
        Date fromD = from == null ? null : Date.from(DateTimeUtils.clientToServer(
                from, clientType, zoneId));
        Date toD = to == null ? null : Date.from(DateTimeUtils.clientToServer(
                to, clientType, zoneId));
        items = (DbConfig.getDbType().equals(DatabaseType.MySQL) ?
                cpeRepository.getDeviceOnlineReportMySql(domainIds, manufacturer, model, fromD, toD) :
                cpeRepository.getDeviceOnlineReportOracle(domainIds, manufacturer, model, fromD, toD))
                .stream()
                .map(r -> OnlineItem.builder()
                        .domain(r.getDomainName())
                        .manufacturer(r.getManufacturerName())
                        .model(r.getModel())
                        .lastSession(r.getDate())
                        .quantity(r.getCount())
                        .build())
                .collect(Collectors.toList());

        final String dateFormat = userService.getUser(session.getUserId(), zoneId).getDateFormat();
        final Map<DeviceOnlineItem, List<OnlineItem>> deviceItems =
                items.stream()
                        .collect(Collectors.groupingBy(i -> DeviceOnlineItem.builder()
                                        .domain(i.getDomain())
                                        .lastSession(getLastSession(
                                                i.getLastSession(),
                                                clientType,
                                                dateFormat))
                                        .build(),
                                LinkedHashMap::new, Collectors.toList()));

        return DeviceOnlineReport.builder()
                .totalQuantity(items.stream().mapToInt(OnlineItem::getQuantity).sum())
                .items(deviceItems.keySet()
                        .stream()
                        .map(item -> item.toBuilder()
                                .items(deviceItems.get(item))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private String getLastSession(final Date date, final ClientType clientType, final String dateFormat) {
        return DateTimeUtils.formatAcsWithDate(
                Instant.ofEpochMilli(date.getTime()), clientType, ZoneId.systemDefault().getId(), dateFormat);
    }

    public DeviceOfflineReport getDeviceOfflineReport(final String token,
                                                      final DeviceOfflineBody body) {
        final Session session = jwtService.getSession(token);
        final Integer domainId = body.getDomainId();
        final Instant from = body.getFrom();

        final ClientType clientType = session.getClientType();
        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        String zoneId = session.getZoneId();
        final List<DeviceOfflineItem> items =
                cpeRepository.getDeviceOfflineReport(domainIds, body.getManufacturer(), body.getModel(),
                                from != null ? DateTimeUtils.clientToServer(
                                        from, clientType, zoneId) : null)
                        .stream()
                        .map(r -> DeviceOfflineItem.builder()
                                .domain(r.getDomainName())
                                .manufacturer(r.getManufacturerName())
                                .model(r.getModel())
                                .quantity(r.getCount())
                                .build())
                        .collect(Collectors.toList());

        return DeviceOfflineReport.builder()
                .totalQuantity(items.stream().mapToInt(DeviceOfflineItem::getQuantity).sum())
                .items(items)
                .build();
    }

    public DeviceEventReport getDeviceEventReport(final String token,
                                                  final DeviceEventBody body) {
        final Session session = jwtService.getSession(token);
        final Integer domainId = body.getDomainId();
        final Instant from = body.getFrom();
        final Instant to = body.getTo();
        final Integer minQuantity = body.getMinQuantity();


        final ClientType clientType = session.getClientType();
        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        String zoneId = session.getZoneId();
        final List<DeviceEventItem> items =
                cpeRepository.getDeviceEventReport(domainIds, body.getManufacturer(), body.getModel(),
                                from != null ? DateTimeUtils.clientToServer(from, clientType, zoneId) : null,
                                to != null ? DateTimeUtils.clientToServer(to, clientType, zoneId) : null,
                                body.getActivityType(), minQuantity != null ? minQuantity : 0L)
                        .stream()
                        .map(r -> DeviceEventItem.builder()
                                .domain(r.getDomainName())
                                .event(r.getActivityType())
                                .quantity(r.getCount())
                                .build())
                        .collect(Collectors.toList());

        return DeviceEventReport.builder()
                .totalQuantity(items.stream().mapToInt(DeviceEventItem::getQuantity).sum())
                .items(items)
                .build();
    }

    public FTPage<ProfileDownloadReport> getProfileDownloadReport(final String token,
                                                                  final ProfileDownloadBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(),
                body.getPageSize(), body.getSorts(), "id");
        final Integer domainId = body.getDomainId();
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final ClientType clientType = session.getClientType();

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        final List<Page<ProfileDownloadReportProjection>> reportPage =
                pageable.stream()
                        .map(p -> profileFileRepository.findAll(body.getId(), domainIds, body.getManufacturer(), body.getModel(), p))
                        .collect(Collectors.toList());

        final FTPage<ProfileDownloadReport> page = new FTPage<>();
        return page.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(reportPage))
                .items(reportPage.stream()
                        .map(Page::getContent)
                        .flatMap(l -> l.stream()
                                .map(r -> reportMapper.buildProfileDownloadReport(session, user,
                                        clientType, r)))
                        .collect(Collectors.toList()))
                .build();
    }

    public FTPage<FirmwareVersionReport> getFirmwareVersionReport(final String token,
                                                                  final FirmwareVersionBody body) {
        jwtService.getSession(token);
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");
        final Integer domainId = body.getDomainId();

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        final List<Page<ProductGroupFirmwareReportProjection>> reportPage =
                pageable.stream()
                        .map(p -> cpeRepository.getFirmwareVersionReport(domainIds, body.getManufacturer(), body.getModel(), p))
                        .collect(Collectors.toList());

        final FTPage<FirmwareVersionReport> page = new FTPage<>();
        return page.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(reportPage))
                .items(reportPage.stream()
                        .map(Page::getContent)
                        .flatMap(l -> l.stream()
                                .map(reportMapper::buildFirmwareVersionReport))
                        .collect(Collectors.toList()))
                .build();
    }

    public FTPage<FileReport> getReportFiles(final String token,
                                             final GeneratedReportsBody body) {

        final Session session = jwtService.getSession(token);
        final Integer pageSize = body.getPageSize();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final List<FileReport> files = getFileReports(session, user, body.getDateIso());
        final List<FileReport> sortedFiles =
                getSortedFiles(files, body.getPageNumbers(), pageSize, getComparator(body.getSorts()));
        final FTPage<FileReport> reportPage = new FTPage<>();

        return reportPage.toBuilder()
                .items(sortedFiles)
                .pageDetails(FTPageDetails.builder()
                        .pageItems(sortedFiles.size())
                        .totalPages(pageSize != null
                                ? (int) Math.ceil((double) files.size() / pageSize)
                                : null)
                        .totalItems((long) files.size())
                        .build())
                .build();
    }

    public FileDatesList getReportFilesDates(String token) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final List<FileReport> files = getFileReports(session, user, null);

        List<FileDates> processedDates = getProcessedFiles(files).stream()
                .map(FileReport::getCreatedIso)
                .map(instant -> instant.atZone(ZoneId.of("UTC")).toLocalDate())
                .distinct()
                .map(date -> date.atStartOfDay(ZoneId.of("UTC")).toInstant())
                .map(date -> new FileDates(date,
                        fixDateTime(DateTimeUtils.format(date,
                                session.getZoneId(),
                                user.getDateFormat(),
                                user.getTimeFormat()))))
                .sorted(Comparator.comparing(FileDates::getDateIso))
                .collect(Collectors.toList());

        return new FileDatesList(processedDates);
    }

    private String fixDateTime(String dateTime) {
        return dateTime.split(" ")[0];
    }

    private List<FileReport> getFileReports(Session session, UserResponse user, Instant date) {
        final Integer userDomainId = domainService.getDomainIdByUserId(session.getUserId()).orElse(null);
        return ReportFileService.getReportFiles(
            domainService.getChildDomainIds(userDomainId),
            session.getClientType(),
            session.getZoneId(),
            date,
            user.getDateFormat(),
            user.getTimeFormat());
    }

    public File getReportFile(final String token, final String link) {
        jwtService.getUserIdByHeaderAuth(token);

        return ReportFileService.getReportFile(link);
    }

    public void deleteReportFiles(final String token, final List<String> links) {
        final Session session = jwtService.getSession(token);

        links.forEach(link -> {
            ReportFileService.deleteReportFile(link);
            wsSender.sendDeleteFileEvent(session.getClientType(), link);
        });
    }

    public UserActivityTypeDescriptionsResponse getUserActivityTypes(final String token) {
        jwtService.getSession(token);

        return new UserActivityTypeDescriptionsResponse(ReportUtils.getUserActivityTypes());
    }

    public DeviceActivityTypeDescriptionsResponse getDeviceUpdateActivityTypes(final String token) {
        jwtService.getSession(token);

        return new DeviceActivityTypeDescriptionsResponse(ReportUtils.getDeviceUpdateActivityTypes());
    }

    private List<FileReport> getSortedFiles(final List<FileReport> files,
                                            final List<Integer> pageNumbers,
                                            final Integer pageSize,
                                            final Comparator<FileReport> comparator) {
        final int pSize = pageSize != null ? pageSize : 0;

        return files.stream()
                .map(file -> file.toBuilder()
                        .domain(domainService.getDomainNameById(
                                file.getDomain() == null ? null : Integer.valueOf(file.getDomain())))
                        .build())
                .sorted(comparator)
                .skip(pageNumbers != null && !pageNumbers.isEmpty() ? pSize * (pageNumbers.get(0) - 1) : 0)
                .limit(pageSize != null ? pageSize : Integer.MAX_VALUE)
                .collect(Collectors.toList());
    }

    private List<FileReport> getProcessedFiles(final List<FileReport> files) {
        return files.stream()
                .map(file -> file.toBuilder()
                        .domain(domainService.getDomainNameById(
                                file.getDomain() == null ? null : Integer.valueOf(file.getDomain())))
                        .build())
                .collect(Collectors.toList());
    }

    private Comparator<FileReport> getComparator(final List<FieldSort> sorts) {
        final String property = sorts == null || sorts.isEmpty() ? null : sorts.get(0).getField();
        final Sort.Direction direction = sorts == null || sorts.isEmpty()
                ? null : Sort.Direction.valueOf(sorts.get(0).getDirection().name());
        Comparator<FileReport> comparator = COMPARATOR_MAP.get(property);
        if (direction == null || direction.isDescending()) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

}
