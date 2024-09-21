package com.friendly.services.management.groupupdate.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.OrderDirection;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.management.groupupdate.dto.response.DevicesStatusResponse;
import com.friendly.services.management.groupupdate.dto.request.GetGroupUpdateGroups;
import com.friendly.services.management.groupupdate.dto.GroupUpdateCondition;
import com.friendly.services.management.groupupdate.dto.GroupUpdateConditionItem;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateDeviceReport;
import com.friendly.services.management.groupupdate.dto.GroupUpdateFilters;
import com.friendly.services.management.groupupdate.dto.GroupUpdateGroup;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateReport;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateSerialResponse;
import com.friendly.services.management.groupupdate.dto.GroupUpdateTask;
import com.friendly.services.management.groupupdate.dto.response.TargetedDevicesResponse;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateDeviceStateType;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType;
import com.friendly.commons.models.reports.GroupUpdateBody;
import com.friendly.commons.models.reports.GroupUpdateDeviceBody;
import com.friendly.commons.models.request.IntIdRequest;
import com.friendly.commons.models.request.IntIdsRequest;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.view.GroupUpdateView;
import com.friendly.commons.models.view.ViewType;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum;
import com.friendly.services.management.groupupdate.mapper.GroupUpdateMapper;
import com.friendly.services.management.groupupdate.utils.strategy.DeviceCompletedUgGroupUpdateStrategy;
import com.friendly.services.management.groupupdate.utils.strategy.DeviceGroupUpdateStrategy;
import com.friendly.services.management.groupupdate.utils.strategy.DevicesAllStrategy;
import com.friendly.services.management.groupupdate.utils.strategy.DevicesByFiltersStrategy;
import com.friendly.services.management.groupupdate.utils.strategy.DevicesIndividualStrategy;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.DeviceStateProjection;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.DevicesStatusProjection;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.UpdateGroupChildProjection;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.UpdateGroupProjection;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupCompletedEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupDeviceEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupTransactionEntity;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.device.activity.orm.acs.repository.TaskRepository;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupChildRepository;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupCompletedRepository;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupDeviceRepository;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupRepository;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupTransactionRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ViewRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.friendly.services.uiservices.view.ViewService;
import com.ftacs.Exception_Exception;
import com.ftacs.IntegerArrayWS;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;
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
public class GroupUpdateService {
    private final ProductClassGroupRepository productClassGroupRepository;

    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final GroupUpdateMapper groupUpdateMapper;
    @NonNull
    private final UpdateGroupChildRepository groupChildRepository;
    @NonNull
    private final UpdateGroupRepository updateGroupRepository;
    @NonNull
    private final UpdateGroupDeviceRepository updateGroupDeviceRepository;
    @NonNull
    private final UpdateGroupCompletedRepository updateGroupCompletedRepository;
    @NonNull
    private final UpdateGroupTransactionRepository updateGroupTransactionRepository;
    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final ViewRepository viewRepository;
    @NonNull
    private final ViewService viewService;


    public FTPage<GroupUpdateReport> getGroupUpdateReport(final String token,
                                                          final GroupUpdateBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");
        final Integer domainId = body.getDomainId();

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        final List<Page<UpdateGroupEntity>> reportPage =
                pageable.stream()
                        .map(p -> updateGroupRepository.findAll(domainIds, body.getManufacturer(),
                                body.getModel(), body.getFrom(), body.getTo(), p))
                        .collect(Collectors.toList());

        final FTPage<GroupUpdateReport> page = new FTPage<>();
        return page.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(reportPage))
                .items(reportPage.stream()
                        .map(Page::getContent)
                        .flatMap(l -> l.stream()
                                .map(r -> groupUpdateMapper.entityToGroupUpdateReport(r,
                                        session.getClientType(),
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())))
                        .collect(Collectors.toList()))
                .build();
    }

    public FTPage<GroupUpdateDeviceReport> getGroupUpdateDeviceReport(final String token,
                                                                      final GroupUpdateDeviceBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(),
                body.getPageSize(), body.getSorts(), "id");
        final Session session = jwtService.getSession(token);
        final Long groupId = body.getGroupId();
        final Integer id = body.getId();
        final String serial = body.getSerial();
        final Boolean searchExact = body.getSearchExact();

        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());

        final FTPage<GroupUpdateDeviceReport> page = new FTPage<>();
        final boolean isIndividual = updateGroupDeviceRepository.existsByUpdateGroupId(id, groupId);
        if (isIndividual) {
            final List<Page<UpdateGroupDeviceEntity>> updateDevices;
            if (searchExact == null || searchExact.equals(Boolean.FALSE)) {
                updateDevices = pageable.stream()
                        .map(p -> updateGroupDeviceRepository
                                .findAllByUpdateGroupIdAndSerialLike(id, groupId, serial, p))
                        .collect(Collectors.toList());
            } else {
                updateDevices = pageable.stream()
                        .map(p -> updateGroupDeviceRepository
                                .findAllByUpdateGroupIdAndSerial(id, groupId, serial, p))
                        .collect(Collectors.toList());
            }
            final List<GroupUpdateDeviceReport> devices =
                    updateDevices.stream()
                            .flatMap(l -> l.stream()
                                    .map(gd -> deviceToGroupUpdateDevice(id, gd.getDevice(),
                                            session.getClientType(),
                                            session.getZoneId(),
                                            user.getDateFormat(),
                                            user.getTimeFormat())))
                            .collect(Collectors.toList());
            return page.toBuilder()
                    .pageDetails(PageUtils.buildPageDetails(updateDevices))
                    .items(devices)
                    .build();
        } else {
            final List<Page<DeviceEntity>> updateDevices;
            if (searchExact == null || searchExact.equals(Boolean.FALSE)) {
                updateDevices = pageable.stream()
                        .map(p -> groupChildRepository
                                .findAllByUpdateGroupIdAndSerialLike(id, groupId, serial, p))
                        .collect(Collectors.toList());
            } else {
                updateDevices = pageable.stream()
                        .map(p -> groupChildRepository
                                .findAllByUpdateGroupIdAndSerial(id, groupId, serial, p))
                        .collect(Collectors.toList());
            }
            final List<GroupUpdateDeviceReport> devices =
                    updateDevices.stream()
                            .flatMap(l -> l.stream()
                                    .map(d -> deviceToGroupUpdateDevice(id, d, session.getClientType(),
                                            session.getZoneId(),
                                            user.getDateFormat(),
                                            user.getTimeFormat())))
                            .collect(Collectors.toList());
            return page.toBuilder()
                    .pageDetails(PageUtils.buildPageDetails(updateDevices))
                    .items(devices)
                    .build();
        }
    }

    private GroupUpdateDeviceReport deviceToGroupUpdateDevice(final Integer updateGroupId, final DeviceEntity d,
                                                              final ClientType clientType, final String zoneId,
                                                              final String dateFormat, final String timeFormat) {
        final Optional<UpdateGroupCompletedEntity> updateCompleted =
                updateGroupCompletedRepository.findByUpdateGroupIdAndDeviceId(updateGroupId, d.getId());
        final List<Long> transactionIds =
                updateGroupTransactionRepository.findAllByUpdateGroupId(updateGroupId)
                        .stream()
                        .map(UpdateGroupTransactionEntity::getTransactionId)
                        .collect(Collectors.toList());
        final List<Long> completedTaskIds = taskRepository.getCompletedTaskIdsByTransactionIdAndDeviceId(transactionIds,
                d.getId());
        final List<Long> failedTaskIds = taskRepository.getFailedTaskIdsByTransactionIdAndDeviceId(transactionIds,
                d.getId());
        final List<Long> pendingTaskIds = taskRepository.getPendingTaskIdsByTransactionIdAndDeviceId(transactionIds,
                d.getId());
        final List<Long> sentTaskIds = taskRepository.getSentTaskIdsByTransactionIdAndDeviceId(transactionIds,
                d.getId());
        final List<Long> rejectedTaskIds = taskRepository.getRejectedTaskIdsByTransactionIdAndDeviceId(transactionIds,
                d.getId());
        final Map<String, GroupUpdateTask> taskMap = new HashMap<>();
        taskMap.put("completedTasks", GroupUpdateTask.builder().ids(completedTaskIds)
                .count(completedTaskIds.size()).build());
        taskMap.put("failedTasks", GroupUpdateTask.builder().ids(failedTaskIds)
                .count(failedTaskIds.size()).build());
        taskMap.put("pendingTasks", GroupUpdateTask.builder().ids(pendingTaskIds)
                .count(pendingTaskIds.size()).build());
        taskMap.put("sentTasks", GroupUpdateTask.builder().ids(sentTaskIds)
                .count(sentTaskIds.size()).build());
        taskMap.put("rejectedTasks", GroupUpdateTask.builder().ids(rejectedTaskIds)
                .count(rejectedTaskIds.size()).build());

        return GroupUpdateDeviceReport.builder()
                .id(d.getId())
                .serial(d.getSerial())
                .manufacturer(d.getProductClass().getProductGroup().getManufacturerName())
                .model(d.getProductClass().getProductGroup().getModel())
                .tasks(taskMap)
                .state(updateCompleted.map(UpdateGroupCompletedEntity::getState)
                        .orElse(GroupUpdateDeviceStateType.NOT_SET))
                .activated(updateCompleted.map(UpdateGroupCompletedEntity::getUpdated)
                        .map(t -> DateTimeUtils.formatAcs(t, clientType,
                                zoneId, dateFormat,
                                timeFormat))
                        .orElse(null))
                .activatedIso(updateCompleted.map(UpdateGroupCompletedEntity::getUpdated)
                        .map(t -> DateTimeUtils.serverToClient(t, clientType, zoneId))
                        .orElse(null))
                .build();
    }

    public FTPage<GroupUpdateGroup> getGroups(final String token, final GetGroupUpdateGroups body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");

        final List<Page<UpdateGroupEntity>> groupsPage =
                pageable.stream()
                        .map(p -> updateGroupRepository.findAll(body.getManufacturer(),
                                body.getModel(), body.getState() == null ? null : body.getState().ordinal(), p))
                        .collect(Collectors.toList());

        final FTPage<GroupUpdateGroup> page = new FTPage<>();
        return page.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(groupsPage))
                .items(groupsPage.stream()
                        .map(Page::getContent)
                        .flatMap(l -> l.stream()
                                .map(r -> groupUpdateMapper.entityToGroupUpdateGroup(r,
                                        session.getClientType(),
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())))
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public boolean deleteGroups(final String token, final IntIdsRequest body) {
        Session session = jwtService.getSession(token);

        IntegerArrayWS integerArrayWS = new IntegerArrayWS();
        integerArrayWS.getId().addAll(body.getIds());
        try {
            AcsProvider.getAcsWebService(session.getClientType()).deleteUpdateGroups(integerArrayWS);
        } catch (Exception_Exception e) {
            log.error("Request: " + body, e);
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }

        return true;
    }

    @Transactional
    public boolean activateGroups(final String token, final IntIdsRequest body) {
        Session session = jwtService.getSession(token);

        body.getIds().forEach(id -> activateGroup(id, session.getClientType()));

        return true;
    }

    private void activateGroup(final Integer id, final ClientType clientType) {
        try {
            AcsProvider.getAcsWebService(clientType).startUpdateGroup(id);
        } catch (Exception_Exception e) {
            log.error("Request: " + id, e);
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public boolean stopGroups(final String token, final IntIdsRequest body) {
        Session session = jwtService.getSession(token);

        body.getIds().forEach(id -> stopGroup(id, session.getClientType()));

        return true;
    }

    private void stopGroup(final Integer id, final ClientType clientType) {
        try {
            AcsProvider.getAcsWebService(clientType).stopUpdateGroup(id);
        } catch (Exception_Exception e) {
            log.error("Request: " + id, e);
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public boolean pauseGroups(final String token, final IntIdsRequest body) {
        Session session = jwtService.getSession(token);

        body.getIds().forEach(id -> pauseGroup(id, session.getClientType()));

        return true;
    }

    private void pauseGroup(final Integer id, final ClientType clientType) {
        try {
            AcsProvider.getAcsWebService(clientType).pauseUpdateGroup(id);
        } catch (Exception_Exception e) {
            log.error("Request: " + id, e);
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public List<GroupUpdateCondition> getConditionItems(String token) {
        jwtService.getSession(token);

        List<GroupUpdateCondition> conditions
                = viewRepository.getGroupUpdateConditions()
                .stream()
                .map(GroupUpdateMapper::viewEntityToGroupUpdateCondition)
                .sorted(Comparator.comparing(GroupUpdateCondition::getName))
                .collect(Collectors.toList());

        addDefaultValues(conditions);

        return conditions;
    }

    private void addDefaultValues(List<GroupUpdateCondition> conditions) {
        conditions.addAll(0, Arrays.asList(
                new GroupUpdateCondition(-1L, "All"),
                new GroupUpdateCondition(-2L, "Individual"),
                new GroupUpdateCondition(-3L, "Import from a file")
        ));
    }

    public FTPage<GroupUpdateSerialResponse> getDevices(String token, GroupUpdateFilters filters) {
        jwtService.getSession(token);
        boolean withSearch = StringUtils.hasText(filters.getSearchParam());
        String searchParam = filters.getSearchExact() || !withSearch ? filters.getSearchParam()
                : "%" + filters.getSearchParam() + "%";


        List<Pageable> pageList =
                PageUtils.createPageRequest(
                        filters.getPageNumbers(),
                        filters.getPageSize(),
                        Collections.singletonList(new FieldSort("id", OrderDirection.DESC)), "id");

        UpdateGroupChildProjection projection = groupChildRepository.findByManufacturerAndModel(
                        filters.getManufacturer(), filters.getModel())
                .orElse(null);

        List<Page<DeviceStateProjection>> devices;

        if (projection != null && projection.getState().equals(GroupUpdateStateType.COMPLETED)) {
            pageList = PageUtils.createPageRequest(
                    filters.getPageNumbers(),
                    filters.getPageSize(),
                    Collections.singletonList(new FieldSort("uc.device.updated", OrderDirection.DESC)), "uc.device.updated");

            devices = pageList.stream()
                    .map(p -> withSearch ?
                            updateGroupCompletedRepository.findAllByUgIdAndSerial(projection.getId(), searchParam, p) :
                            updateGroupCompletedRepository.findAllByUgId(projection.getId(), p))
                    .collect(Collectors.toList());
        } else {
            Long groupId = projection == null ? productClassGroupRepository.findIdByManufacturerNameAndModel(
                            filters.getManufacturer(),
                            filters.getModel())
                    .orElseThrow(() -> new FriendlyIllegalArgumentException(
                            ServicesErrorRegistryEnum.PRODUCT_CLASS_GROUP_NOT_FOUND,
                            filters.getManufacturer(), filters.getModel()))
                    : projection.getGroupId();

            pageList = PageUtils.createPageRequest(
                    filters.getPageNumbers(),
                    filters.getPageSize(),
                    Collections.singletonList(new FieldSort("d.updated", OrderDirection.DESC)), "d.updated");

            switch (filters.getSourceType()) {
                case All:
                case Individual:
                    devices = pageList.stream()
                            .map(p -> withSearch ?
                                    updateGroupDeviceRepository.findAllDevicesByGroupIdAndSerial(groupId, searchParam, p) :
                                    updateGroupDeviceRepository.findAllDevicesByGroupId(groupId, p))
                            .collect(Collectors.toList());
                    break;
                case Group:
                    //TODO implement
                default:
                    devices = Collections.emptyList();
            }
        }
        FTPage<GroupUpdateSerialResponse> result = new FTPage<>();
        return result.toBuilder()
                .items(devices.stream()
                        .flatMap(page -> page.getContent().stream())
                        .map(groupUpdateMapper::deviceStateEntityToResponse)
                        .collect(Collectors.toList()))
                .pageDetails(PageUtils.buildPageDetails(devices))
                .build();

//        UpdateGroupProjection projection = updateGroupRepository.getUgAndChildUgIds(
//                filters.getManufacturer(),
//                filters.getModel()).get(0);
//
//        DeviceGroupUpdateStrategy strategy = getStrategy(filters, projection);

//        return strategy.getDevices(filters, p.get(0), projection);
    }


    private DeviceGroupUpdateStrategy getStrategy(GroupUpdateFilters filters, UpdateGroupProjection projection) {
        if (projection.getState().equals(GroupUpdateDeviceStateType.COMPLETED.name())) {
            return new DeviceCompletedUgGroupUpdateStrategy(this);
        } else {
            switch (filters.getSourceType()) {
                case All:
                    return new DevicesAllStrategy(this);
                case Group:
                    return new DevicesByFiltersStrategy(this);
                case Individual:
                    return new DevicesIndividualStrategy(this);
                default:
                    return new DevicesAllStrategy(this);
            }
        }
    }


    public List<GroupUpdateSerialResponse> getDeviceByFilters(final GroupUpdateFilters filters,
                                                              final Pageable pageable) {
        return null;
    }

    public List<GroupUpdateSerialResponse> getDevicesIndividual(final GroupUpdateFilters filters,
                                                                final Pageable p, final UpdateGroupProjection projection) {
        String searchParam = filters.getSearchParam();
        if (!filters.getSearchExact()) {
            searchParam += "%";
        }

        return updateGroupRepository.findIndividualDevicesForSelect(
                        projection.getUgId(),
                        projection.getUgChildId(),
                        searchParam,
                        p
                ).stream()
                .map(groupUpdateMapper::entityToGroupUpdateSerial)
                .collect(Collectors.toList());
    }

    public List<GroupUpdateSerialResponse> getDevicesCompletedUG(final GroupUpdateFilters filters,
                                                                 final Pageable p, final UpdateGroupProjection projection) {
        String searchParam = filters.getSearchParam();
        if (!filters.getSearchExact()) {
            searchParam += "%";
        }

        return updateGroupRepository.findCompletedDevicesForSelect(
                        projection.getUgId(),
                        projection.getUgChildId(),
                        searchParam,
                        p
                ).stream()
                .map(groupUpdateMapper::entityToGroupUpdateSerial)
                .collect(Collectors.toList());
    }

    public List<GroupUpdateSerialResponse> getDevicesAll(final GroupUpdateFilters filters,
                                                         final Pageable p) {
        String searchParam = filters.getSearchParam();
        if (!filters.getSearchExact()) {
            searchParam += "%";
        }

        return updateGroupRepository.findAllDevicesForSelect(
                        filters.getModel(),
                        filters.getManufacturer(),
                        searchParam,
                        p
                ).stream()
                .map(groupUpdateMapper::entityToGroupUpdateSerial)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createGroupUpdateView(String token, GroupUpdateConditionItem body) {

        viewService.createOrUpdateView(token,
                GroupUpdateView.builder()
                        .type(ViewType.GroupUpdateView)
                        .name(body.getName())
                        .isDefaultPublic(false)
                        .isDefaultUser(false)
                        .conditions(body.getConditions())
                        .build());
    }

    public TargetedDevicesResponse targetedDevices(String token, IntIdsRequest body) {
        jwtService.getSession(token);

        return TargetedDevicesResponse.builder()
                .items(updateGroupDeviceRepository.countByUpdateGroupIds(body.getIds()))
                .build();
    }

    public DevicesStatusResponse devicesStatus(String token, IntIdRequest body) {
        jwtService.getSession(token);

        Map<GroupUpdateDeviceStateType, Long> map  =
                updateGroupCompletedRepository.findDevicesStatus(body.getId())
                        .stream()
                        .collect(Collectors.toMap(DevicesStatusProjection::getState, DevicesStatusProjection::getCount));

        return DevicesStatusResponse.builder()
                .completed(Optional.ofNullable(map.get(GroupUpdateDeviceStateType.COMPLETED)).orElse(0L))
                .failed(Optional.ofNullable(map.get(GroupUpdateDeviceStateType.FAILED)).orElse(0L))
                .offline(Optional.ofNullable(map.get(GroupUpdateDeviceStateType.OFFLINE)).orElse(0L))
                .notSent(Optional.ofNullable(map.get(GroupUpdateDeviceStateType.NOT_SET)).orElse(0L))
                .pending(Optional.ofNullable(map.get(GroupUpdateDeviceStateType.PENDING)).orElse(0L))
                .skipped(Optional.ofNullable(map.get(GroupUpdateDeviceStateType.SKIPPED)).orElse(0L))
                .build();
    }
}
