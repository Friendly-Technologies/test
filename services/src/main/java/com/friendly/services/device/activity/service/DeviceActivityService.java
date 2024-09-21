package com.friendly.services.device.activity.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.cache.CpeParameterNameCache;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.FTPageDetails;
import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.device.DeleteDeviceHistoryBody;
import com.friendly.commons.models.device.DeviceActivity;
import com.friendly.commons.models.device.DeviceActivityBody;
import com.friendly.commons.models.device.DeviceActivityTaskBody;
import com.friendly.commons.models.device.DeviceHistory;
import com.friendly.commons.models.device.DeviceHistoryBody;
import com.friendly.commons.models.device.DeviceHistoryDetailsBody;
import com.friendly.commons.models.device.DeviceHistoryDetailsResponse;
import com.friendly.commons.models.device.DeviceLog;
import com.friendly.commons.models.device.FTTaskTypesEnum;
import com.friendly.commons.models.device.NotificationType;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.TaskStateType;
import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.response.DeviceActivityTaskNamesResponse;
import com.friendly.commons.models.device.response.DeviceHistoryActivityTypesResponse;
import com.friendly.commons.models.device.setting.Parameter;
import com.friendly.commons.models.settings.DatabaseType;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.device.activity.orm.acs.model.DeviceActivityDetailsEntity;
import com.friendly.services.device.activity.orm.acs.model.DeviceActivitySetAttribDetailsEntity;
import com.friendly.services.device.activity.orm.acs.repository.DeviceActivityDetailsRepository;
import com.friendly.services.device.activity.orm.acs.repository.TaskRepository;
import com.friendly.services.device.activity.orm.acs.repository.TransactionRepository;
import com.friendly.services.device.diagnostics.orm.acs.repository.DeviceDiagnosticsRepository;
import com.friendly.services.device.history.orm.acs.model.DeviceHistoryEntity;
import com.friendly.services.device.history.orm.acs.repository.DeviceHistoryRepository;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.device.info.model.TaskParam;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.info.utils.DeviceActivityUtil;
import com.friendly.services.device.method.orm.acs.repository.CpeMethodNameRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.CpeParameterNameValueProjection;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import com.friendly.services.device.provision.orm.acs.repository.DeviceProvisionObjectRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.NumberConversionRegistry;
import com.friendly.services.infrastructure.utils.PageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendly.commons.models.device.TaskStateType.COMPLETED;
import static com.friendly.commons.models.device.TaskStateType.FAILED;
import static com.friendly.commons.models.device.TaskStateType.PENDING;
import static com.friendly.commons.models.device.TaskStateType.REJECTED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.TASK_NAME_NOT_FOUND;
import static com.friendly.services.device.info.utils.DeviceActivityUtil.getDeviceActivityComparator;
import static com.friendly.services.device.info.utils.DeviceActivityUtil.getDeviceActivitySort;
import static com.friendly.services.device.info.utils.DeviceActivityUtil.getTaskParamComparator;

/**
 * Service that exposes the base functionality for interacting with {@link DeviceActivity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceActivityService {
    private final CpeMethodNameRepository cpeMethodNameRepository;

    @NonNull
    private final DeviceMapper deviceMapper;
    @NonNull
    private final UserService userService;
    @NonNull
    private final DeviceDiagnosticsRepository deviceDiagnosticsRepository;
    @NonNull
    private final TransactionRepository transactionRepository;
    @NonNull
    private final DeviceActivityDetailsRepository deviceActivityDetailsRepository;
    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final DeviceHistoryRepository deviceHistoryRepository;
    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final ParameterNameService parameterNameService;
    @NonNull
    private final ParameterService parameterService;
    @NonNull
    final CpeRepository cpeRepository;
    @NonNull
    final DeviceProvisionObjectRepository objectRepository;

    @NonNull
    private final CpeParameterNameCache cpeParameterNameCache;
    public List<DeviceTab> getActivityTabs(final Long deviceId) {
        final List<DeviceTab> tabs = new ArrayList<>();

        tabs.add(DeviceTab.builder()
                .name("Activity")
                .path("device-activity")
                .build());

        tabs.add(DeviceTab.builder()
                .name("Device history")
                .path("device-history")
                .build());

        if (parameterService.isLogsExistsByDeviceId(deviceId)) {
            tabs.add(DeviceTab.builder()
                    .name("Device log")
                    .path("device-log")
                    .build());
        }
        return tabs;
    }

    public FTPage<DeviceHistory> getDeviceHistory(final String token, DeviceHistoryBody body) {
        final Session session = jwtService.getSession(token);
        List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(),
                body.getPageSize(), body.getSorts(), "id");

        Instant date = null, to = null;
        String zoneId = session.getZoneId();
        Instant utcDate = body.getDate();
        if (utcDate != null) {
            Instant client = DateTimeUtils.addZoneId(utcDate, zoneId);
            date = DateTimeUtils.clientToServer(client, session.getClientType(), zoneId);
            to = date.plus(1, ChronoUnit.DAYS);
        }
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), zoneId);


        Instant finalDate = date;
        Instant finalTo = to;
        final List<Page<DeviceHistoryEntity>> deviceHistoryPage =
                pageable.stream()
                        .map(p -> deviceHistoryRepository.getDeviceHistory(body.getDeviceId(), body.getActivityType(),
                                finalDate, finalTo, p))
                        .collect(Collectors.toList());
        final List<DeviceHistory> deviceHistory =
                deviceHistoryPage.stream()
                        .map(Page::getContent)
                        .flatMap(h -> deviceMapper.deviceHistoryEntitiesToDeviceHistories(h,
                                        session.getClientType(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList());

        final FTPage<DeviceHistory> page = new FTPage<>();
        return page.toBuilder()
                .items(deviceHistory)
                .pageDetails(PageUtils.buildPageDetails(deviceHistoryPage))
                .build();
    }

    @Transactional
    public void deleteDeviceHistory(final String token, DeleteDeviceHistoryBody body) {
        jwtService.getSession(token);
        //TODO: change to delete by ACS
        List<Long> deviceIds = body.getHistoryIds();
        Long deviceId = body.getDeviceId();


        deviceIds.forEach(id -> {
            if (deviceHistoryRepository.existsByIdAndDeviceId(id, deviceId)) {
                deviceHistoryRepository.deleteByIdAndDeviceId(id, deviceId);
            }
        });
    }

    public DeviceHistoryActivityTypesResponse getDeviceHistoryActivityTypes(final String token, final Long deviceId) {
        jwtService.getSession(token);

        return new DeviceHistoryActivityTypesResponse(deviceHistoryRepository.getDeviceHistoryActivityTypes(deviceId));
    }

    public DeviceLog getDeviceLog(final String token, final Long deviceId) {
        jwtService.getSession(token);
        return DeviceLog.builder()
                .log(parameterService.getDeviceLog(deviceId))
                .build();
    }


    public FTPage<DeviceActivity> getDeviceActivity(final String token, DeviceActivityBody body) {
        final Session session = jwtService.getSession(token);

        List<FieldSort> sorts = body.getSorts();
        List<Integer> pageNumbers = body.getPageNumbers();
        Integer pageSize = body.getPageSize();

        /*if (sorts != null) {
            sorts.forEach(s -> s.setField(DeviceUtils.getDeviceActivitySort(s.getField())));
        }*/
        final List<Pageable> pageable = PageUtils.createPageRequest(pageNumbers, pageSize, sorts, "id");
        final String property = sorts == null || sorts.isEmpty() ? null : sorts.get(0).getField();
        final String direction = sorts == null || sorts.isEmpty() ? "DESC" : sorts.get(0).getDirection().name();
        final String sortField = getDeviceActivitySort(property);
        final int skip = pageNumbers != null && !pageNumbers.isEmpty() ? pageSize * (pageNumbers.get(0) - 1) : 0;
        final int limit = pageSize != null ? pageSize : Integer.MAX_VALUE;
        final String taskName = body.getTaskName();
        final Set<String> nameTask = taskName == null ? null : DeviceActivityUtil.getDeviceActivityKey(taskName);

        if (nameTask == null) {
            return getDeviceActivityPage(body.getDeviceId(), body.getIds(), body.getTaskState(), null,
                    body.getFrom(), body.getTo(), body.getPageSize(), direction, session, sortField, skip, limit);
        }

        List<FTPage<DeviceActivity>> pages = nameTask.stream()
                .map(task -> getDeviceActivityPage(body.getDeviceId(), body.getIds(), body.getTaskState(), task,
                        body.getFrom(), body.getTo(), body.getPageSize(), direction, session, sortField, skip, limit))
                .collect(Collectors.toList());

        long totalItems = pages.stream()
                .map(page -> page.getPageDetails().getTotalItems())
                .mapToLong(Long::longValue)
                .sum();
        int totalPageItems = pages.stream()
                .map(page -> page.getPageDetails().getPageItems())
                .mapToInt(Integer::intValue)
                .sum();
        Collection<DeviceActivity> items = pages.stream()
                .map(FTPage::getItems)
                .flatMap(Collection::stream)
                .limit(Math.min(limit, totalPageItems))
                .collect(Collectors.toList());

        FTPage<DeviceActivity> deviceActivityPage = new FTPage<>();

        deviceActivityPage = deviceActivityPage.toBuilder()
                .pageDetails(FTPageDetails.builder()
                        .pageItems(Math.min(limit, totalPageItems))
                        .totalItems(totalItems)
                        .totalPages((int) Math.ceil((double) totalItems / limit))
                        .build())
                .items(items)
                .build();

        return deviceActivityPage;
    }

    public DeviceActivityTaskNamesResponse getDeviceActivityTaskNames(final String token, final Long deviceId) {
        jwtService.getSession(token);

        List<String> taskNames = taskRepository.getDeviceActivityTaskNames(deviceId)
                .stream()
                .map(this::getTaskNameId)
                .distinct()
                .map(DeviceActivityUtil::getDeviceActivityName)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        return new DeviceActivityTaskNamesResponse(taskNames);
    }

    @Transactional
    public void deleteDeviceActivity(final String token, DeviceActivityTaskBody body) {
        jwtService.getSession(token);
        Long deviceId = body.getDeviceId();
        List<Long> taskIds = body.getTaskIds();

        //TODO: change to delete by ACS
        taskIds.forEach(id -> {
            if (taskRepository.deleteCompletedTask(deviceId, id) > 0) {
                return;
            }
            if (taskRepository.deletePendingTask(deviceId, id) > 0) {
                return;
            }
            if (taskRepository.deleteRejectedTask(deviceId, id) > 0) {
                return;
            }
            taskRepository.deleteFailedTask(deviceId, id);
        });
    }

    private FTPage<DeviceActivity> getDeviceActivityPage(final Long cpeId, final List<Long> ids,
                                                         final TaskStateType taskState, final String nameTask,
                                                         final Instant from, final Instant to,
                                                         final Integer pageSize, final String direction,
                                                         final Session session, final String sortField,
                                                         final int skip, final int limit) {
        final FTPage<DeviceActivity> deviceActivityPage = new FTPage<>();


        if (taskState == null) {
            final DatabaseType dbType = DbConfig.getDbType();
            switch (dbType) {
                case MySQL:
                    final List<DeviceActivity> deviceActivity = new ArrayList<>();
                    final Long totalItems = taskRepository.getDeviceActivityCount(cpeId, nameTask, from, to, ids)
                            + taskRepository.getAllDeviceActivityPendingCount(cpeId, nameTask, from, to, ids);
                    if (sortField.startsWith("comparator.")) {
                        deviceActivity.addAll(
                                (direction.equals("ASC")
                                        ? taskRepository.getDeviceActivityASC(cpeId, nameTask, from, to,
                                        0, Integer.MAX_VALUE, "id", ids)
                                        : taskRepository.getDeviceActivityDESC(cpeId, nameTask, from, to,
                                        0, Integer.MAX_VALUE, "id", ids))
                                        .stream()
                                        .map(a -> getDeviceActivity(cpeId, session, a, direction, sortField))
                                        .sorted(getDeviceActivityComparator(
                                                Sort.Direction.valueOf(direction), sortField))
                                        .skip(skip)
                                        .limit(limit)
                                        .collect(Collectors.toList()));
                    } else {
                        final Comparator<DeviceActivity> ascNameComparator =
                                Comparator.comparing(DeviceActivity::getTaskName, String.CASE_INSENSITIVE_ORDER);
                        final Comparator<DeviceActivity> descNameComparator =
                                ascNameComparator.reversed();
                        deviceActivity.addAll(
                                (direction.equals("ASC")
                                        ? taskRepository.getDeviceActivityASC(cpeId, nameTask, from, to,
                                        skip, limit, sortField, ids)
                                        : taskRepository.getDeviceActivityDESC(cpeId, nameTask, from, to,
                                        skip, limit, sortField, ids))
                                        .stream()
                                        .map(a -> getDeviceActivity(cpeId, session, a, direction, sortField))
                                        .collect(Collectors.toList()));
                        if (sortField.equals("task_name")) {
                            deviceActivity.sort(direction.equals("ASC") ?
                                    ascNameComparator : descNameComparator);
                        }
                    }
                    final int pageItems = deviceActivity.size();
                    return deviceActivityPage.toBuilder()
                            .items(deviceActivity)
                            .pageDetails(FTPageDetails.builder()
                                    .totalPages(pageSize != null ? (int)
                                            Math.ceil((double) totalItems / pageSize)
                                            : null)
                                    .totalItems(totalItems)
                                    .pageItems(pageItems)
                                    .build())
                            .build();
                case Oracle:
                    final List<FTPage<DeviceActivity>> fullTasks =
                            Stream.of(COMPLETED, FAILED, REJECTED, PENDING)
                                    .map(t -> getDeviceActivityPage(session, cpeId, ids, t, from, to, nameTask,
                                            direction, sortField, deviceActivityPage, pageSize, limit, skip))
                                    .collect(Collectors.toList());
                    final List<DeviceActivity> tasks = fullTasks.stream()
                            .flatMap(f -> f.getItems().stream())
                            .sorted(getDeviceActivityComparator(
                                    Sort.Direction.valueOf(direction), sortField))
                            .skip(skip)
                            .limit(limit)
                            .collect(Collectors.toList());
                    final Long total = fullTasks.stream()
                            .mapToLong(f -> f.getPageDetails().getTotalItems())
                            .sum();

                    return deviceActivityPage.toBuilder()
                            .items(tasks)
                            .pageDetails(FTPageDetails.builder()
                                    .totalPages(pageSize != null ? (int)
                                            Math.ceil((double) total / pageSize)
                                            : null)
                                    .totalItems(total)
                                    .pageItems(tasks.size())
                                    .build())
                            .build();

                default:
                    return null;
            }
        } else {
            return getDeviceActivityPage(session, cpeId, ids, taskState, from, to, nameTask,
                    direction, sortField, deviceActivityPage, pageSize, limit, skip);
        }
    }

    private FTPage<DeviceActivity> getDeviceActivityPage(final Session session, final Long cpeId,
                                                         final List<Long> ids, final TaskStateType taskState,
                                                         final Instant from, final Instant to,
                                                         final String nameTask,
                                                         final String direction, final String sortField,
                                                         final FTPage<DeviceActivity> deviceActivityPage,
                                                         final Integer pageSize, final int limit, final int skip) {
        final List<DeviceActivity> deviceActivity;
        final Long totalItems;
        final int pageItems;
        switch (taskState) {
            case COMPLETED:
                totalItems = taskRepository.getDeviceActivityCompletedCount(cpeId, nameTask, from, to, ids);
                break;
            case FAILED:
                totalItems = taskRepository.getDeviceActivityFailedCount(cpeId, nameTask, from, to, ids);
                break;
            case REJECTED:
                totalItems = taskRepository.getDeviceActivityRejectedCount(cpeId, nameTask, from, to, ids);
                break;
            case PENDING:
                totalItems = taskRepository.getDeviceActivityPendingCount(cpeId, nameTask, from, to, ids);
                break;
            case SENT:
                totalItems = taskRepository.getDeviceActivitySentCount(cpeId, nameTask, from, to, ids);
                break;
            default:
                return null;
        }
        deviceActivity = getDeviceActivityList(session, cpeId, ids, from, to,
                nameTask, direction, sortField, limit, skip, taskState);
        pageItems = deviceActivity.size();
        return deviceActivityPage.toBuilder()
                .items(deviceActivity)
                .pageDetails(FTPageDetails.builder()
                        .totalPages(pageSize != null ? (int)
                                Math.ceil((double) totalItems / pageSize)
                                : null)
                        .totalItems(totalItems)
                        .pageItems(pageItems)
                        .build())
                .build();
    }

    private List<DeviceActivity> getDeviceActivityList(final Session session, final Long cpeId, final List<Long> ids,
                                                       final Instant from, final Instant to, final String nameTask,
                                                       final String direction, final String sortField, final int limit,
                                                       final int skip, TaskStateType taskState) {

        List<DeviceActivity> deviceActivity = new ArrayList<>();
        List<Object[]> activities = new ArrayList<>();
        final Comparator<DeviceActivity> ascNameComparator =
                Comparator.comparing(DeviceActivity::getTaskName, String.CASE_INSENSITIVE_ORDER);
        final Comparator<DeviceActivity> descNameComparator =
                ascNameComparator.reversed();
        switch (taskState) {
            case COMPLETED:
                activities = direction.equals("ASC") ?
                        taskRepository.getDeviceActivityCompletedASC(cpeId, nameTask, from, to,
                                sortField.startsWith("comparator.") ? 0 : skip,
                                sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                                nameTask, ids)
                        : taskRepository.getDeviceActivityCompletedDESC(cpeId, nameTask, from, to,
                        sortField.startsWith("comparator.") ? 0 : skip,
                        sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                        nameTask, ids);
                break;
            case FAILED:
                activities = direction.equals("ASC") ?
                        taskRepository.getDeviceActivityFailedASC(cpeId, nameTask, from, to,
                                sortField.startsWith("comparator.") ? 0 : skip,
                                sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                                nameTask, ids)
                        : taskRepository.getDeviceActivityFailedDESC(cpeId, nameTask, from, to,
                        sortField.startsWith("comparator.") ? 0 : skip,
                        sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                        nameTask, ids);
                break;
            case REJECTED:
                activities = direction.equals("ASC") ?
                        taskRepository.getDeviceActivityRejectedASC(cpeId, nameTask, from, to,
                                sortField.startsWith("comparator.") ? 0 : skip,
                                sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                                nameTask, ids)
                        : taskRepository.getDeviceActivityRejectedDESC(cpeId, nameTask, from, to,
                        sortField.startsWith("comparator.") ? 0 : skip,
                        sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                        nameTask, ids);
                break;
            case PENDING:
                activities = direction.equals("ASC") ?
                        taskRepository.getDeviceActivityPendingASC(cpeId, nameTask, from, to,
                                sortField.startsWith("comparator.") ? 0 : skip,
                                sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                                nameTask, ids)
                        : taskRepository.getDeviceActivityPendingDESC(cpeId, nameTask, from, to,
                        sortField.startsWith("comparator.") ? 0 : skip,
                        sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                        nameTask, ids);
                break;
            case SENT:
                activities = direction.equals("ASC") ?
                        taskRepository.getDeviceActivitySentASC(cpeId, nameTask, from, to,
                                sortField.startsWith("comparator.") ? 0 : skip,
                                sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                                nameTask, ids)
                        : taskRepository.getDeviceActivitySentDESC(cpeId, nameTask, from, to,
                        sortField.startsWith("comparator.") ? 0 : skip,
                        sortField.startsWith("comparator.") ? Integer.MAX_VALUE : limit,
                        nameTask, ids);
                break;
        }
        if (sortField.startsWith("comparator.")) {
            deviceActivity.addAll(
                    (activities.stream()
                            .map(a -> getDeviceActivity(cpeId, session, a, direction, sortField))
                            .sorted(getDeviceActivityComparator(
                                    Sort.Direction.valueOf(direction), sortField))
                            .skip(skip)
                            .limit(limit)
                            .collect(Collectors.toList())));
        } else {
            deviceActivity.addAll(
                    (activities.stream()
                            .map(a -> getDeviceActivity(cpeId, session, a, direction, sortField))
                            .collect(Collectors.toList())));
            if (sortField.equals("task_name")) {
                deviceActivity.sort(direction.equals("ASC") ?
                        ascNameComparator : descNameComparator);
            }
        }

        return deviceActivity;
    }
    private DeviceActivity getDeviceActivity(Long cpeId, final Session session, final Object[] taskParam,
                                             final String direction, final String sortField) {
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        List<TaskParam> p = getTaskParam(cpeId,
                NumberConversionRegistry.convertToInt(taskParam[6]),
                NumberConversionRegistry.convertToLong(taskParam[1]),
                NumberConversionRegistry.convertToLong(taskParam[5]),
                NumberConversionRegistry.convertToLong(taskParam[7]),
                (String) taskParam[2],
                direction, sortField);
        return DeviceActivity.builder()
                .taskId(NumberConversionRegistry.convertToLong(taskParam[1]))
                .taskState(TaskStateType.fromValue((String) taskParam[0]))
                .taskName(DeviceActivityUtil.getDeviceActivityName(taskParam[2] == null
                        ? null
                        : getTaskNameId((String) taskParam[2])))
                .createdIso(((Timestamp) taskParam[3]).toInstant())
                .created(DateTimeUtils.formatAcs(((Timestamp) taskParam[3]).toInstant(),
                        session.getClientType(), null, user.getDateFormat(), user.getTimeFormat()))
                .completedIso(taskParam[4] == null ? null : ((Timestamp) taskParam[4]).toInstant())
                .completed(taskParam[4] == null ? null
                        : DateTimeUtils.formatAcs(((Timestamp) taskParam[4]).toInstant(),
                        session.getClientType(), null, user.getDateFormat(), user.getTimeFormat()))
                .creator(p == null || p.isEmpty() || p.get(0).getCreator() == null
                        ? null : p.get(0).getCreator().contains("/")
                        ? StringUtils.substringAfter(p.get(0).getCreator(), "/")
                        : p.get(0).getCreator())
                .application(p == null || p.isEmpty() || p.get(0).getCreator() == null
                        ? null : p.get(0).getCreator().contains("/")
                        ? StringUtils.substringBefore(p.get(0).getCreator(), "/")
                        : null)
                .parameters(p == null || p.isEmpty()
                        ? null : p.stream()
                        .map(tp -> Parameter.builder()
                                .fullName(tp.getName())
                                .value(tp.getValue())
                                .build())
                        .sorted(Comparator.comparing((Parameter param) -> param.getValue() == null).reversed()
                                .thenComparing(Parameter::getFullName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList()))
                .errorCode(taskParam.length < 9 ? null : (Integer) taskParam[8])
                .errorText(taskParam.length < 10 ? null : (String) taskParam[9])
                .build();
    }

    private String getTaskNameId(final String taskName) {
        if (StringUtils.isEmpty(taskName)) {
            throw new FriendlyIllegalArgumentException(TASK_NAME_NOT_FOUND);
        }

        int length = taskName.length();

        int startIndex = 0;
        while (startIndex < length && !Character.isLetter(taskName.charAt(startIndex))) {
            startIndex++;
        }

        int endIndex = length;
        for (int i = startIndex; i < length; i++) {
            char symbol = taskName.charAt(i);
            if (symbol == '|' || symbol == '>') {
                endIndex = i;
                break;
            }
        }

        return taskName.substring(startIndex, endIndex);
    }


    private List<TaskParam> getTaskParam(Long cpeId, final Integer typeId, final Long taskId, final Long transactionId,
                                         final Long taskKey, final String taskName,
                                         final String direction, final String sortField) {
        final List<TaskParam> taskParams = new ArrayList<>();
        FTTaskTypesEnum type = FTTaskTypesEnum.getByCode(typeId);
        boolean sort = true;
        switch (type) {
            case ExecuteMethod: {
                String[] numbers = StringUtils.substringAfter(taskName, "|")
                        .split("\\|");
                if (numbers.length > 1) {
                    taskParams.add(TaskParam.builder()
                            .name(cpeMethodNameRepository.getCpeMethodNameEntityById(Long.parseLong(numbers[0])).getName())
                            .value(numbers[1])
                            .creator(transactionRepository.getCreatorFromTransaction(transactionId))
                            .build());
                }
                else {
                    taskParams.add(TaskParam.builder()
                            .name(cpeMethodNameRepository.getCpeMethodNameEntityById(Long.parseLong(numbers[0])).getName())
                            .creator(transactionRepository.getCreatorFromTransaction(transactionId))
                            .build());
                }
                break;
            }
            case DownloadUpdateGroup:
            case DownloadProfile:
            case Download:
            case Upload:
            case UploadUpdateGroup:
            case BackupCpeConfiguration:
            case RestoreCpeConfiguration:
            case BackupProfile:
                taskParams.addAll(deviceActivityDetailsRepository.getCreatorFromFileHistory(taskId)
                        .stream()
                        .map(deviceMapper::activityDetailsToTaskParam)
                        .collect(Collectors.toList()));
                break;
            case Reboot:
            case FactoryReset:
            case DiscoverParameterValueList:
            case ReProvisionUpdateGroup:
                taskParams.add(TaskParam.builder()
                        .creator(transactionRepository.getCreatorFromTransaction(transactionId))
                        .build());
                break;
            case SetParameterValues:
            case SetParameterValuesProfile:
            case SetParameterValuesProvision:
            case SetParameterValuesUpdateGroup:
            case WiFiChannelRescanQoE:
            case SetPeriodic_QoE:
                taskParams.addAll(deviceActivityDetailsRepository.getTaskParamFromProvisionHistory(taskId)
                        .stream()
                        .peek(p -> p.setValue(ParameterUtil.maskIfNeeded(p.getName(), p.getValue())))
                        .map(deviceMapper::activityDetailsToTaskParam)
                        .collect(Collectors.toList()));
                break;
            case GetParameterNames:
            case GetParameterNamesOnly:
            case GetParameterNamesRetrieve:
            case ObserveParameter:
            case GetParameterValuesProfileCollector:
            case GetParameterValuesForCpeCollector:
            case GetParameterValueList:
            case GetParameterAttributesList:
            case GetParamUpdateGroup:
                final String creator = transactionRepository.getCreatorFromTransaction(transactionId);
                String taskNameParam = taskKey == 0 ? "" : parameterNameService.getNameById(taskKey);
                if ((taskNameParam != null && !taskNameParam.isEmpty()) || !taskName.contains("|")) {
                    taskNameParam = (taskNameParam != null && !taskNameParam.isEmpty()) ? taskNameParam : "";
                    taskParams.add(TaskParam.builder()
                            .name(taskNameParam)
                            .creator(creator)
                            .build());
                } else {
                    String[] numbers = StringUtils.substringAfter(taskName, "|")
                            .split("\\|");
                    Arrays.stream(numbers)
                            .forEach(number -> taskParams.add(TaskParam.builder()
                                    .name(parameterNameService.getNameById(Long.parseLong(number)))
                                    .creator(creator)
                                    .build()));
                }
                break;
            case SetParameterValuesObjectProfile:
            case SetParameterValuesObjectProvision:
                taskParams.addAll(deviceActivityDetailsRepository.getTaskParamFromObjectParamHistory(taskId)
                        .stream()
                        .peek(p -> p.setValue(ParameterUtil.maskIfNeeded(p.getName(), p.getValue())))
                        .map(deviceMapper::activityDetailsToTaskParam)
                        .collect(Collectors.toList()));
                break;
            case RequestDiagnostic:
                final String creator1 = transactionRepository.getCreatorFromTransaction(transactionId);
                String diagName = deviceDiagnosticsRepository.getTaskNameFromCpeDiagnostic(taskKey);
                DiagnosticType diagnosticType = diagName == null ? null : DiagnosticType.fromPartialName(diagName);
                diagName = diagnosticType == null ? diagName : diagnosticType.getDescription();
                taskParams.add(TaskParam.builder()
                        .name(diagName)
                        .creator(creator1)
                        .build());
                break;
            case DiagnosticComplete:
                taskParams.add(TaskParam.builder()
                        .name("Get diagnostic result")
                        .build());
                break;
            case AddObjectProvision:
            case AddObjectProfile:
                taskParams.addAll(getDetailsForAddObject(cpeId, taskId, taskKey, direction, sortField));
                sort = false;
                break;
            case SetParameterNotificationProfile:
            case SetParameterAccessProfile:
            case SetParameterAttributesProvision:
            case SetNotification_QoE:
            case SetParameterAttributesUpdateGroup:
                taskParams.addAll(getDetailsForSetAttrib(cpeId, taskId)
                        .stream()
                        .map(deviceMapper::activityDetailsToTaskParam)
                        .collect(Collectors.toList()));
                break;
            case DeleteObject:
                taskParams.addAll(deviceActivityDetailsRepository.getTaskParamFromDeleteObject(taskKey)
                        .stream()
                        .map(p -> TaskParam.builder()
                                .name(p.getName())
                                .creator(p.getCreator())
                                .build())
                        .collect(Collectors.toList()));
                break;
            case CustomRPCUpdateGroup:
            case CustomRPC:
                taskParams.addAll(deviceActivityDetailsRepository.getTaskParamFromCustomRpcHistory(taskId)
                        .stream()
                        .map(deviceMapper::activityDetailsToTaskParam)
                        .collect(Collectors.toList()));
                break;
            case ChangeDUState:
            case ChangeDUStateUpdateGroup:
                taskParams.addAll(deviceActivityDetailsRepository.getTaskParamFromOp(taskId)
                        .stream()
                        .map(deviceMapper::activityDetailsToTaskParam)
                        .collect(Collectors.toList()));
                break;
            case DiagnosticUpdateGroup:
                taskParams.addAll(deviceHistoryRepository.getValueAndNameIdFromProvHistory(taskId).stream().map(proj ->
                        TaskParam.builder()
                                .name(cpeParameterNameCache.getNameById(proj.getNameId()))
                                .value(proj.getValue())
                                .build()).collect(Collectors.toList()));
                break;
            default:
                return Collections.emptyList();
        }
        return sort ? taskParams.stream()
                .sorted(getTaskParamComparator(Sort.Direction.valueOf(direction), sortField))
                .collect(Collectors.toList()) : taskParams;
    }

    public DeviceHistoryDetailsResponse getDeviceHistoryDetails(String token, DeviceHistoryDetailsBody body) {
        final Session session = jwtService.getSession(token);
        String zoneId = session.getZoneId();
        final UserResponse user = userService.getUser(session.getUserId(), zoneId);
        return new DeviceHistoryDetailsResponse(deviceHistoryRepository.getDeviceHistoryDetails(body.getId())
                .stream()
                .map(details -> deviceMapper.entityToDeviceHistoryDetails(details, session.getClientType(),
                        user.getDateFormat(), user.getTimeFormat(), zoneId))
                .collect(Collectors.toList()));
    }

    private List<TaskParam> getDetailsForAddObject(Long cpeId, Long taskId, Long taskKey, String direction, String sortField) {
//        ProtocolType protocolType = ProtocolType.fromValue(cpeRepository.getProtocolTypeByDevice(cpeId).orElse(0));
        List<DeviceActivityDetailsEntity> detailsList = deviceActivityDetailsRepository.getTaskParamFromObjectHistory(taskId);
        List<TaskParam> taskParams = new ArrayList<>();
        if (!detailsList.isEmpty()) {
            DeviceActivityDetailsEntity details = detailsList.get(0);
            taskParams.add(deviceMapper.activityDetailsToTaskParam(details));

            List<CpeParameterNameValueProjection> params = objectRepository.findParamsByProvisionId(taskKey);
            taskParams.addAll(params.stream()
                    .map(p -> TaskParam.builder()
                            .name(p.getName())
                            .value(p.getValue())
                            .creator(details.getCreator())
                            .build())
                    .sorted(getTaskParamComparator(Sort.Direction.valueOf(direction), sortField))
                    .collect(Collectors.toList()));
        }
        return taskParams;
    }

    public List<DeviceActivityDetailsEntity> getDetailsForSetAttrib(Long cpeId, Long taskId) {
        List<DeviceActivitySetAttribDetailsEntity> detailsAttribEntities = deviceActivityDetailsRepository
                .getTaskParamForSetAttribute(taskId);

        ProtocolType protocolType = ProtocolType.fromValue(cpeRepository.getProtocolTypeByDevice(cpeId).orElse(0));
        List<DeviceActivityDetailsEntity> detailsEntities = new ArrayList<>();
        for (DeviceActivitySetAttribDetailsEntity details : detailsAttribEntities) {
            String value = "";
            if (!StringUtils.isEmpty(details.getAccessList())) {
                value = "Access list \"" +
                        (details.getAccessList().contains("Subscriber") ? "All" : "ACS Only") + "\"";
            }
            if (details.getNotification() != null) {
                value = value.isEmpty() ? value : " ";
                value += protocolType.equals(ProtocolType.TR069) ? "Notification \"" : "Observation \"";
                NotificationType notificationType = NotificationType.fromValue(protocolType, details.getNotification());
                value += notificationType.equals(NotificationType.UNKNOWN) ?
                        protocolType.equals(ProtocolType.TR069) ? "" : details.getNotification() :
                        notificationType.getDescription();
                value += "\"";
            }

      if (value.matches("Observation \"(\\d)\"")) {
          value = processObservation(value);
      }
        detailsEntities.add(
            DeviceActivityDetailsEntity.builder()
                .name(details.getName())
                .value(value)
                .creator(details.getCreator())
                .build());
        }
        return detailsEntities;
    }

    public static String processObservation(String observation) {
        int number = Integer.parseInt(observation.substring(observation.length() - 2, observation.length() - 1));

        if (number == 1 || number == 2) {
            return "Observation \"On\"";
        } else {
            return "Observation \"Off\"";
        }
    }
}
