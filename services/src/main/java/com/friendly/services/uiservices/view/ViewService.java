package com.friendly.services.uiservices.view;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.DeviceColumns;
import com.friendly.commons.models.device.DeviceExtParamsBody;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.view.AbstractView;
import com.friendly.commons.models.view.ConditionItem;
import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.commons.models.view.FrameView;
import com.friendly.commons.models.view.FrameViewRequest;
import com.friendly.commons.models.view.GetDeviceViewsListRequest;
import com.friendly.commons.models.view.GroupUpdateView;
import com.friendly.commons.models.view.ListView;
import com.friendly.commons.models.view.PropertyType;
import com.friendly.commons.models.view.SearchView;
import com.friendly.commons.models.view.ViewColumn;
import com.friendly.commons.models.view.ViewCondition;
import com.friendly.commons.models.view.ViewFrame;
import com.friendly.commons.models.view.ViewFrameRequest;
import com.friendly.commons.models.view.ViewSimple;
import com.friendly.commons.models.view.ViewType;
import com.friendly.commons.models.view.response.ConditionsResponse;
import com.friendly.commons.models.view.response.DeviceColumnsResponse;
import com.friendly.commons.models.view.response.ViewsSimpleResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.info.utils.DeviceUpdateColumn;
import com.friendly.services.device.info.utils.DeviceViewUtil;
import com.friendly.services.device.info.utils.helper.QueryViewHelper;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.qoemonitoring.orm.iotw.model.QoeFrameItemEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnConditionEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ConditionItemEntity;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameConditionEntity;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewFrameEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewUserEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewUserPK;
import com.friendly.services.device.info.orm.acs.repository.DeviceRepository;
import com.friendly.services.qoemonitoring.orm.iotw.repository.QoeFrameItemRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ColumnConditionRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ColumnRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ConditionItemRepository;
import com.friendly.services.uiservices.frame.orm.iotw.repository.FrameConditionRepository;
import com.friendly.services.uiservices.frame.orm.iotw.repository.FrameRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ViewFrameRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ViewRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ViewUserRepository;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.uiservices.view.mapper.ViewMapper;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.friendly.commons.models.device.DeviceDisplayType.FRAME;
import static com.friendly.commons.models.view.ConditionLogic.And;
import static com.friendly.commons.models.view.ConditionType.Equal;
import static com.friendly.commons.models.view.ConditionType.IsNull;
import static com.friendly.commons.models.websocket.ActionType.CREATE;
import static com.friendly.commons.models.websocket.ActionType.DELETE;
import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PARAMETER_NOT_UNIQUE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.VIEW_NOT_EXIST;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.VIEW_NOT_FOUND;
import static com.friendly.services.device.info.utils.ColumnId.CREATED;
import static com.friendly.services.device.info.utils.ColumnId.MANUFACTURER;
import static com.friendly.services.device.info.utils.ColumnId.MODEL;
import static com.friendly.services.device.info.utils.ColumnId.SERIAL;
import static com.friendly.services.device.info.utils.ColumnId.STATUS;
import static com.friendly.services.device.info.utils.ColumnId.UPDATED;
import static com.friendly.services.device.info.utils.helper.QueryViewHelper.getPredicateFromConditions;

/**
 * Service that exposes the base functionality for interacting with {@link AbstractView} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewService {
    @NonNull
    private final QoeFrameItemRepository qoeFrameItemRepository;

    @NonNull
    private final FrameConditionRepository frameConditionRepository;

    @NonNull
    private final ConditionItemRepository conditionItemRepository;

    @NonNull
    private final DeviceRepository deviceRepository;

    @NonNull
    private final ViewMapper viewMapper;

    @NonNull
    private final UserService userService;

    @NonNull
    private final DomainService domainService;

    @NonNull
    private final ViewRepository viewRepository;

    @NonNull
    private final ViewUserRepository viewUserRepository;

    @NonNull
    private final ColumnRepository columnRepository;

    @NonNull
    private final ColumnConditionRepository columnConditionRepository;

    @NonNull
    private final FrameRepository frameRepository;

    @NonNull
    private final ViewFrameRepository viewFrameRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final WsSender wsSender;

    @Transactional
    public ViewsSimpleResponse getViews(final String token, final GetDeviceViewsListRequest request) {
        final Session session = jwtService.getSession(token);
        final Optional<Integer> userDomain = domainService.getDomainIdByUserId(session.getUserId());
        final UserResponse userResponse = userService.getUserByIdWithoutDomain(session.getUserId(),
                session.getZoneId());
        final String locale = userResponse.getLocaleId() == null ? "EN" : userResponse.getLocaleId();


        final Long deviceId = request.getDeviceId();
        final ViewType viewType = request.getViewType();

        final List<Object[]> objects;

        if (!userDomain.isPresent() || userDomain.get() == 0) {
            objects = viewRepository.getSimpleViewsForSuperDomain(session.getClientType(), viewType);
        } else {
            objects = viewRepository.getSimpleViewsForDomain(session.getClientType(), viewType, userDomain.get());
        }

        AtomicReference<Boolean> isDevicePrioritySet = new AtomicReference<>(false);

        final List<ViewSimple> views =
            sortViewsByIgnoringCase(
                objects.stream()
                    .map(
                        v -> {
                          boolean priority = false;
                          if (Boolean.FALSE.equals(isDevicePrioritySet.get())) {
                            priority =
                                viewType == ViewType.FrameView
                                    && deviceId != null
                                    && checkDevicePriority(
                                        deviceId,
                                        (Long) v[0],
                                        locale,
                                        session.getZoneId(),
                                        userResponse.getDateFormat(),
                                        userResponse.getTimeFormat(),
                                        session);
                            isDevicePrioritySet.set(priority);
                          }
                          return viewMapper.fieldsToViewSimples(
                              v,
                              isPresentViewUser((Long) v[0], session.getUserId()),
                              priority);
                        })
                    .collect(Collectors.toList()));

        List<ViewSimple> viewsSimple = sortViewsByIgnoringCase(setDefaultView(views.isEmpty() ? createDefaultView(session, viewType, userDomain.orElse(null))
                : views, viewType, session.getUserId(), userDomain.orElse(null), session.getClientType()));
        return new ViewsSimpleResponse(viewsSimple);
    }

    private boolean checkDevicePriority(final Long deviceId, final Long viewId,
                                        final String locale, final String zoneId,
                                        final String dateFormat, final String timeFormat,
                                        final Session session) {
        List<ConditionItem> conditionItems = getConditionItems(locale, zoneId, dateFormat, timeFormat, null);

        List<ViewCondition> viewConditions = findAllConditionItemsByViewId(conditionItems, viewId);
        if (viewConditions.equals(Collections.emptyList())) {
          return false;
        }
        viewConditions.add(getDeviceIdCondition(deviceId));

        List<DeviceEntity> devicesByConditions = deviceRepository.findAll(
                getListFilters(getParamsForFiltering(viewId, viewConditions),
                        session.getClientType(), session.getZoneId()));

        return !devicesByConditions.isEmpty();
    }

    private static ViewCondition getDeviceIdCondition(Long deviceId) {
        return ViewCondition.builder()
                .columnKey("deviceId")
                .compare(Equal)
                .compareName("==")
                .conditionString(deviceId.toString())
                .logic(And)
                .build();
    }

    private DeviceExtParamsBody getParamsForFiltering(Long viewId, List<ViewCondition> viewConditions) {
        return DeviceExtParamsBody.builder()
                .displayType(FRAME)
                .viewId(viewId)
                .conditions(viewConditions)
                .build();
    }

    public Specification<DeviceEntity> getListFilters(final DeviceExtParamsBody params,
                                                      final ClientType clientType, final String zoneId) {
        final Long viewId = params.getViewId();

        return (root, cq, cb) -> {
            final Predicate mainPredicate = QueryViewHelper.getPredicate(null, null, null, root, cb);
            return getFilter(root, cb, params.getConditions(), viewId, ConditionLogic.And, mainPredicate,
                    clientType, cq, zoneId);
        };
    }

    private List<ViewCondition> findAllConditionItemsByViewId(final List<ConditionItem> conditionItems,
                                                                  final Long viewId) {
        for (ConditionItem conditionItem : conditionItems) {
            if (conditionItem.getViewId().equals(viewId)) {
                return conditionItem.getConditions();
            }
        }
        return Collections.emptyList();
    }

    private List<ViewSimple> sortViewsByIgnoringCase(List<ViewSimple> views){
        Comparator<ViewSimple> comparator = (view1, view2) -> {
            if(view1.getName().equalsIgnoreCase("Default")){
                return -1;
            }
            else if(view2.getName().equalsIgnoreCase("Default")){
                return 1;
            }
            boolean view1IsNumeric = Character.isDigit(view1.getName().charAt(0));
            boolean view2IsNumeric = Character.isDigit(view2.getName().charAt(0));
            if (view1IsNumeric && !view2IsNumeric) {
                return -1;
            } else if (!view1IsNumeric && view2IsNumeric) {
                return 1;
            }

            int flag = Character.compare(Character.toLowerCase(view1.getName().charAt(0)), Character.toLowerCase(view2.getName().charAt(0)));
            if (flag != 0) return flag;

            return view1.getName().compareTo(view2.getName());
        };

        return views.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private boolean isPresentViewUser(Long viewId, Long userId) {
        return viewUserRepository.findById(new ViewUserPK(viewId, userId)).isPresent();
    }

    public AbstractView getView(final String token, final Long viewId) {
        final Session session = jwtService.getSession(token);

        return getAbstractView(viewId, session);
    }

    @Transactional
    public AbstractView createOrUpdateView(final String token, final AbstractView view) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final ViewEntity viewEntity = saveViewEntity(view, view.getType(), session, user);
        final AbstractView abstractView;
        switch (view.getType()) {
            case DeviceView:
                abstractView = createOrUpdateListView(session, viewEntity.getId(), ((ListView) view), user);
                break;
            case SearchView:
                abstractView = createOrUpdateSearchView(viewEntity.getId(), ((SearchView) view), user);
                break;
            case FrameView:
                abstractView = createOrUpdateFrameView(viewEntity.getId(), (FrameViewRequest) view);
                break;
            case GroupUpdateView:
                abstractView = createOrUpdateGroupUpadteView(viewEntity.getId(), (GroupUpdateView) view);
                break;
            default:
                throw new FriendlyEntityNotFoundException(VIEW_NOT_FOUND, viewEntity.getId());
        }
        if (view.getId() == null) {
            wsSender.sendViewEvent(clientType, CREATE, abstractView);
        } else {
            wsSender.sendViewEvent(clientType, UPDATE, abstractView);
        }
        return abstractView;
    }

    private AbstractView createOrUpdateGroupUpadteView(Long viewId, GroupUpdateView view) {
        final List<Long> conditionIds = new ArrayList<>();
        final List<ViewCondition> conditions = view.getConditions();

        collectConditionIds(conditionIds, conditions);
        if (conditionIds.isEmpty()) {
            columnConditionRepository.deleteAllByViewId(viewId);
        } else {
            columnConditionRepository.deleteAllByViewIdAndIdNotIn(viewId, conditionIds);
        }

        if (conditions != null && !conditions.isEmpty()) {
            conditions.forEach(c -> saveCondition(c, viewId, null));
        }

        return getGroupUpdateView(viewId);
    }


    @Transactional
    public void deleteViews(final String token, final List<Long> viewIds) {
        final ClientType clientType = jwtService.getClientTypeByHeaderAuth(token);

        viewIds.forEach(id -> {
            final Optional<ViewEntity> viewEntity = viewRepository.findById(id);
            if (viewEntity.isPresent()) {
                viewRepository.deleteById(id);
                final ViewType type = viewEntity.get().getType();
                switch (type) {
                    case DeviceView:
                        columnRepository.deleteAllByViewId(id);
                        columnConditionRepository.deleteAllByViewId(id);
                        break;
                    case SearchView:
                        columnRepository.deleteAllByViewId(id);
                        break;
                    case FrameView:
                        frameConditionRepository.deleteAllByViewId(id);
                        viewFrameRepository.deleteAllByViewId(id);
                        break;
                    case GroupUpdateView:
                        columnConditionRepository.deleteAllByViewId(id);
                        break;
                }
                wsSender.sendViewEvent(clientType, DELETE, id);
            }
        });
    }

    public void validateView(final Long viewId, final ViewType type) {
        if (!viewRepository.getByIdAndType(viewId, type).isPresent()) {
            throw new FriendlyEntityNotFoundException(VIEW_NOT_EXIST);
        }
    }

    public Predicate getFilter(final Root<DeviceEntity> root,
                               final CriteriaBuilder cb,
                               final Long viewId,
                               final ConditionLogic logic,
                               final Predicate parentPredicate,
                               final Long parentId,
                               final ClientType clientType, CriteriaQuery<?> cq,
                               final String zoneId) {

        final List<Predicate> predicates =
                columnConditionRepository.findAllByViewIdAndParentId(viewId, parentId)
                        .stream()
                        .map(c -> c.getLogic().equals(And)
                                ? cb.and(getCondition(root, cb, viewId, c, clientType, cq, zoneId))
                                : cb.or(getCondition(root, cb, viewId, c, clientType, cq, zoneId)))
                        .collect(Collectors.toList());
        return getPredicateFromConditions(cb, logic, parentPredicate, predicates);
    }

    public Predicate getFilter(final Root<DeviceEntity> root,
                               final CriteriaBuilder cb,
                               final Long viewId,
                               final ConditionLogic logic,
                               final Predicate parentPredicate,
                               final ClientType clientType,
                               final List<ViewCondition> conditions, CriteriaQuery<?> cq,
                               final String zoneId) {
        final List<ColumnConditionEntity> cond = conditions.stream()
                .map(co -> viewMapper.conditionToConditionEntity(co, viewId, null))
                .collect(Collectors.toList());


        final List<Predicate> predicates = cond.stream()
                .map(c -> cb.and(getCondition(root, cb, viewId, c, clientType, cq, zoneId)))
                .collect(Collectors.toList());
        return getPredicateFromConditions(cb, logic, parentPredicate, predicates);
    }



    public Predicate getFilter(final Root<DeviceEntity> root,
                               final CriteriaBuilder cb,
                               final List<ViewCondition> conditions,
                               final Long viewId,
                               final ConditionLogic logic,
                               final Predicate parentPredicate,
                               final ClientType clientType, CriteriaQuery<?> cq,
                               final String zoneId) {
        final List<ColumnConditionEntity> viewConditions = columnConditionRepository.findAllByViewIdAndParentId(viewId, null);

        if (conditions != null && !conditions.isEmpty()) {
            getConditionsForFiltering(conditions, viewId, viewConditions);
        }

        final List<Predicate> predicates = viewConditions.stream()
                .map(c -> c.getLogic().equals(And)
                        ? cb.and(getCondition(root, cb, viewId, c, clientType, cq, zoneId))
                        : cb.or(getCondition(root, cb, viewId, c, clientType, cq, zoneId)))
                .collect(Collectors.toList());
        return getPredicateFromConditions(cb, logic, parentPredicate, predicates);
    }

    private void getConditionsForFiltering(List<ViewCondition> conditions, Long viewId, List<ColumnConditionEntity> viewConditions) {
        if(conditions == null) {
            return;
        }
        List<ColumnConditionEntity> conditionEntities;
        conditionEntities = conditions.stream()
                .map(condition -> viewMapper.conditionToConditionEntity(condition, viewId, null))
                .collect(Collectors.toList());
        viewConditions.addAll(conditionEntities);
        conditions.forEach(c -> getConditionsForFiltering(c.getItems(), viewId, viewConditions));
    }

    public List<ViewColumn> getViewColumns(final Long viewId, final String localeId) {
        return columnRepository.findAllByViewId(viewId)
                .stream()
                .map(c -> viewMapper.columnEntityToColumn(c, localeId))
                .sorted(Comparator.nullsLast(
                        Comparator.comparing(ViewColumn::getIndexVisible,
                                Comparator.nullsLast(Comparator.naturalOrder()))))
                .collect(Collectors.toList());
    }

    public DeviceColumnsResponse getDeviceColumns(final String token) {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final String localeId = Optional.ofNullable(userService.getUserByIdWithoutDomain(userId, session.getZoneId()))
                .map(UserResponse::getLocaleId)
                .orElse("EN");

        List<DeviceColumns> columns = DeviceUpdateColumn.getDeviceUpdateColumnsList()
                .stream()
                .map(c -> c.toBuilder()
                        .columnName(DeviceViewUtil.getColumnName(c.getColumnKey(), localeId))
                        .build())
                .sorted(Comparator.comparing(DeviceColumns::getColumnName))
                .collect(Collectors.toList());
        return new DeviceColumnsResponse(columns);
    }

    public ConditionsResponse getFiltersByColumn(final String token, final String columnKey) {
        jwtService.getUserIdByHeaderAuth(token);

        return new ConditionsResponse(DeviceViewUtil.getFiltersByColumn(columnKey));
    }


    private List<ViewSimple> setDefaultView(final List<ViewSimple> views, final ViewType viewType, final Long userId,
                                            final Integer domainId, final ClientType clientType) {
        return views.size() == 1 ? views.stream()
                .map(v -> v.toBuilder().isDefault(true).build())
                .collect(Collectors.toList())
                : getDefaultViewByClient(views, viewType, userId, domainId, clientType);
    }

    private List<ViewSimple> getDefaultViewByClient(final List<ViewSimple> views, final ViewType viewType,
                                                    final Long userId, final Integer domainId,
                                                    final ClientType clientType) {
        final Optional<Long> userViewId = viewRepository.getDefaultUserView(viewType, userId);
        if (userViewId.isPresent()) {
            return setDefaultView(views, userViewId.get());
        } else {
            return setDefaultView(views,
                    viewRepository.getDefaultDomainView(viewType, clientType, domainId)
                            .orElseGet(() -> viewRepository.getSimpleViews(clientType, viewType, domainId)
                                    .stream()
                                    .findFirst()
                                    .map(v -> (Long) v[0])
                                    .orElse(null)));
        }
    }

    private List<ViewSimple> setDefaultView(final List<ViewSimple> views, final Long viewId) {
        if (viewId == null) {
            return views;
        }
        return views.stream()
                .map(v -> {
                    if (v.getId().equals(viewId)) {
                        return v.toBuilder().isDefault(true).build();
                    } else return v;
                })
                .collect(Collectors.toList());
    }

    public AbstractView getAbstractView(final Long viewId, final Session session) {
        final Optional<ViewEntity> viewEntity = viewRepository.findById(viewId);
        if (viewEntity.isPresent()) {
            switch (viewEntity.get().getType()) {
                case DeviceView:
                    return getListView(viewId, session,
                            userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId()));
                case SearchView:
                    return getSearchView(viewId, userService.getUserByIdWithoutDomain(session.getUserId(),
                            session.getZoneId()));
                case FrameView:
                    return getFrameView(viewId, session.getUserId());
                case GroupUpdateView:
                    return getGroupUpdateView(viewId);
            }
        }
        throw new FriendlyEntityNotFoundException(VIEW_NOT_FOUND, viewId);
    }

    private SearchView getSearchView(final Long viewId, final UserResponse user) {
        return viewRepository.findById(viewId)
                .map(v -> viewMapper.viewEntityToSearchView(v, isPresentViewUser(viewId, user.getId())))
                .map(v -> setViewColumns(v, user.getLocaleId() != null ? user.getLocaleId() : "EN"))
                .orElseThrow(() -> new FriendlyEntityNotFoundException(VIEW_NOT_FOUND, viewId));
    }

    private ListView getListView(final Long viewId, final Session session, final UserResponse user) {
        return viewRepository.findById(viewId)
                .map(v -> viewMapper.viewEntityToListView(v, isPresentViewUser(viewId, user.getId())))
                .map(v -> setColumns(v, user.getLocaleId() != null ? user.getLocaleId() : "EN",
                        session.getZoneId(), user.getDateFormat(), user.getTimeFormat()))
                .orElseThrow(() -> new FriendlyEntityNotFoundException(VIEW_NOT_FOUND, viewId));
    }

    private FrameView getFrameView(final Long viewId) {
        return viewRepository.findById(viewId)
                .map(viewMapper::viewEntityToFrameView)
                .map(view -> view.toBuilder()
                        .frames(viewFrameRepository.findAllByViewIdOrderByIndex(view.getId())
                                .stream()
                                .map(this::getViewFrame)
                                .collect(Collectors.toList()))
                        .build())
                .orElseThrow(() -> new FriendlyEntityNotFoundException(VIEW_NOT_FOUND, viewId));
    }

    private FrameView getFrameView(final Long viewId, Long userId) {
        return viewRepository.findById(viewId)
                .map(v -> viewMapper.viewEntityToFrameView(v, isPresentViewUser(viewId, userId)))
                .map(view -> view.toBuilder()
                        .frames(getFrames(view.getId()))
                        .build())
                .orElseThrow(() -> new FriendlyEntityNotFoundException(VIEW_NOT_FOUND, viewId));
    }

    private GroupUpdateView getGroupUpdateView(Long viewId) {
        return viewRepository.findById(viewId)
                .map(viewMapper::viewEntityToGroupUpdateView)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(VIEW_NOT_FOUND, viewId));
    }

    private List<ViewFrame> getFrames(final Long id) {
        return viewFrameRepository.findAllByViewIdOrderByIndex(id)
                .stream()
                .map(this::getViewFrame)
                .collect(Collectors.toList());
    }

    private AbstractView createOrUpdateListView(final Session session, final Long viewId,
                                                final ListView view, final UserResponse user) {

        updateColumns(viewId, view.getColumns());

        final List<Long> conditionIds = new ArrayList<>();
        final List<ViewCondition> conditions = view.getConditions();

        collectConditionIds(conditionIds, conditions);
        if (conditionIds.isEmpty()) {
            columnConditionRepository.deleteAllByViewId(viewId);
        } else {
            columnConditionRepository.deleteAllByViewIdAndIdNotIn(viewId, conditionIds);
        }

        if (conditions != null && !conditions.isEmpty()) {
            conditions.forEach(c -> saveCondition(c, viewId, null));
        }

        return getListView(viewId, session, user);
    }

    private void collectConditionIds(final List<Long> ids, final List<ViewCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return;
        }
        ids.addAll(conditions.stream()
                .map(ViewCondition::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        conditions.forEach(c -> collectConditionIds(ids, c.getItems()));
    }

    private void saveCondition(final ViewCondition condition, final Long viewId, final Long parentId) {
        if (condition == null) {
            return;
        }
        final ColumnConditionEntity conditionEntity = columnConditionRepository.save(
                viewMapper.conditionToConditionEntity(condition, viewId, parentId));

        condition.getItems()
                .forEach(c -> saveCondition(c, viewId, conditionEntity.getId()));
    }

    private AbstractView createOrUpdateFrameView(final Long viewId, final FrameViewRequest view) {
        final List<ViewFrameRequest> frames = view.getFrames();
        final List<ViewFrameEntity> framesToDel;

        if (frames == null || frames.isEmpty()) {
            framesToDel = viewFrameRepository.findAllByViewId(viewId);
        } else {
            final List<Long> frameIds = frames.stream()
                    .map(ViewFrameRequest::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            framesToDel = viewFrameRepository.findAllByViewIdAndFrameIdNotIn(viewId, frameIds);
            final List<ViewFrameEntity> framesToUpd =
                    frames.stream()
                            .map(frame -> viewFrameRepository.save(
                                                ViewFrameEntity.builder()
                                                    .viewId(viewId)
                                                    .frameId(frame.getId())
                                                    .index(frame.getIndex())
                                                    .propertyType(frame.getType())
                                                    .build()
                                    )
                            )
                            .collect(Collectors.toList());
            viewFrameRepository.saveAll(framesToUpd);

        }
        final List<Long> frameIds = framesToDel.stream()
                .map(ViewFrameEntity::getFrameId)
                .collect(Collectors.toList());
        if (!frameIds.isEmpty()) {
            viewFrameRepository.deleteAllByViewIdAndFrameIdIn(viewId, frameIds);
        }

        return getFrameView(viewId);
    }

    private AbstractView createOrUpdateSearchView(final Long viewId, final SearchView view, final UserResponse user) {
        updateColumns(viewId, view.getColumns());
        return getSearchView(viewId, user);
    }

    private void updateColumns(final Long viewId, final List<ViewColumn> columns) {
        columnRepository.deleteAllByViewId(viewId);
        columnRepository.saveAll(viewMapper.columnsToColumnEntities(columns, viewId));
    }

    private ViewEntity saveViewEntity(final AbstractView view, final ViewType viewType,
                                      final Session session, final UserResponse user) {
        Integer domainId = user.getDomainId();
        Optional<Long> foundId;
        if (domainId == null || domainId == 0) {
            foundId = viewRepository.findIdByNameClientTypeAndViewTypeForSuperDomain(view.getName(), viewType, user.getClientType());
        } else {
            foundId = viewRepository.findIdByNameClientTypeAndViewTypeForSuperDomain(view.getName(), viewType, user.getClientType(), user.getDomainId());
        }
        if (isViewNameNotUnique(view.getId(), foundId)) {
            throw new FriendlyIllegalArgumentException(PARAMETER_NOT_UNIQUE, view.getName());
        }

        final ViewEntity entity = viewMapper.viewToViewEntity(view, session.getClientType(), user.getDomainId());//todo: here
        if (view instanceof FrameViewRequest) {
            entity.setTableWidth(((FrameViewRequest) view).getTableWidth());
        }
        final ViewEntity viewEntity = viewRepository.save(entity);
        if (view.isDefaultUser()) {
            final List<Long> viewIds =
                    viewUserRepository.getAllByUserId(user.getId())
                            .stream()
                            .map(ViewUserEntity::getViewId)
                            .map(viewId -> viewRepository.getByIdAndType(viewId, viewType))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(ViewEntity::getId)
                            .collect(Collectors.toList());

            viewIds.forEach(viewId -> viewUserRepository.deleteById(new ViewUserPK(viewId, user.getId())));

            viewUserRepository.saveAndFlush(ViewUserEntity.builder()
                    .userId(user.getId())
                    .viewId(viewEntity.getId())
                    .build());
        }
        if (!view.isDefaultUser() && isPresentViewUser(view.getId(), user.getId())) {
            viewUserRepository.deleteById(new ViewUserPK(view.getId(), user.getId()));
        }
        if (view.isDefaultPublic()) {
            viewRepository.resetDefaultDomain(viewType, session.getClientType(), user.getDomainId(), viewEntity.getId());
        }
        return viewEntity;
    }

    private boolean isViewNameNotUnique(Long currentId, Optional<Long> foundId) {
        return foundId.isPresent() && currentId != null && !foundId.get().equals(currentId)
                || currentId == null && foundId.isPresent();
    }

    private Predicate getCondition(final Root<DeviceEntity> root, final CriteriaBuilder cb,
                                   final Long viewId, final ColumnConditionEntity condition,
                                   final ClientType clientType, CriteriaQuery<?> cq,
                                   final String zoneId) {
        final Predicate predicate = condition.getColumnKey().equals("domainId")
                ? getPredicateFromDomain(root, cb, condition)
                : DeviceViewUtil.getPredicateFromCondition(root, cb, condition, clientType, cq, zoneId);

        return getFilter(root, cb, viewId, condition.getLogic(), predicate, condition.getId(), clientType, cq, zoneId);
    }

    private Predicate getPredicateFromDomain(final Root<DeviceEntity> root, final CriteriaBuilder cb,
                                             final ColumnConditionEntity condition) {
        if (condition.getStringValue() == null) {
            if (condition.getType() == IsNull) {
                return cb.isNull(root.get("domainId"));
            }
        }
        final int domainId = Integer.parseInt(condition.getStringValue());
        switch (condition.getType()) {
            case InHierarchy:
                return domainId == 0 || domainId == -1
                        ? cb.and()
                        : cb.in(root.get("domainId")).value(domainService.getChildDomainIds(domainId));
            case NotEqual:
                return domainId == 0 || domainId == -1
                        ? cb.and(cb.in(root.get("domainId")).value(Arrays.asList(0, -1)).not(),
                        cb.isNotNull(root.get("domainId")))
                        : cb.notEqual(root.get("domainId"), domainId);
            default: //Equal
                return domainId == 0 || domainId == -1
                        ? cb.or(cb.in(root.get("domainId")).value(Arrays.asList(0, -1)),
                        cb.isNull(root.get("domainId")))
                        : cb.equal(root.get("domainId"), domainId);
        }
    }


    private SearchView setViewColumns(final SearchView searchView, final String localeId) {
        return searchView.toBuilder()
                .columns(getViewColumns(searchView.getId(), localeId))
                .build();
    }

    private ListView setColumns(final ListView listView, final String localeId, final String zoneId,
                                final String dateFormat, final String timeFormat) {
        return listView.toBuilder()
                .columns(getViewColumns(listView.getId(), localeId))
                .conditions(getColumnsConditions(listView.getId(), localeId, zoneId, dateFormat, timeFormat))
                .build();
    }

    private ViewFrame getViewFrame(final ViewFrameEntity viewFrameEntity) {
        final Long frameId = viewFrameEntity.getFrameId();
        if(viewFrameEntity.getPropertyType() == PropertyType.QOE) {
            return getViewQoeFrame(viewFrameEntity);
        }
        final FrameEntity frameEntity = frameRepository.findById(frameId)
                .orElse(null);
        if (frameEntity == null) {
            return null;
        }

        return ViewFrame.builder()
                .id(frameId)
                .index(viewFrameEntity.getIndex())
                .name(frameEntity.getName())
                .icon(frameEntity.getIcon())
                .isDefault(frameEntity.getIsDefault())
                .type(
                        Boolean.TRUE.equals(frameEntity.getIsDefault()) ? PropertyType.DEFAULT : PropertyType.CUSTOM)
                .build();
    }

    private ViewFrame getViewQoeFrame(final ViewFrameEntity viewFrameEntity) {
        final QoeFrameItemEntity qoeFrameEntity = qoeFrameItemRepository.findById(viewFrameEntity.getFrameId())
                .orElse(null);
        if (qoeFrameEntity == null) {
            return null;
        }

        return ViewFrame.builder()
                .id(viewFrameEntity.getFrameId())
                .index(viewFrameEntity.getIndex())
                .name(qoeFrameEntity.getName())
                .type(PropertyType.QOE)
                .parameterName(qoeFrameEntity.getParameterName())
                .build();
    }


    private List<ViewCondition> getColumnsConditions(final Long viewId, final String localeId, final String zoneId,
                                                     final String dateFormat, final String timeFormat) {
        return getColumnsConditions(viewId, localeId, zoneId, dateFormat, timeFormat, null);
    }

    private List<ViewCondition> getColumnsConditions(final Long viewId, final String localeId, final String zoneId,
                                                     final String dateFormat, final String timeFormat,
                                                     final Long parentId) {
        final List<ViewCondition> viewConditions =
                columnConditionRepository.findAllByViewIdAndParentId(viewId, parentId)
                        .stream()
                        .map(c -> DeviceViewUtil.conditionEntityToCondition(c,
                                localeId,
                                zoneId,
                                dateFormat,
                                timeFormat))
                        .collect(Collectors.toList());

        viewConditions.forEach(c -> c.setItems(getColumnsConditions(viewId, localeId, zoneId,
                dateFormat, timeFormat, c.getId())));

        return viewConditions;
    }

    private List<ViewSimple> createDefaultView(final Session session, final ViewType viewType, final Integer domainId) {
        final ClientType clientType = session.getClientType();

        if (viewType == ViewType.SearchView || viewType == ViewType.DeviceView) {
            final Long viewId = viewRepository.saveAndFlush(ViewEntity.builder()
                            .name("Default")
                            .domainId(domainId)
                            .type(viewType)
                            .clientType(clientType)
                            .defaultDomain(true)
                            .defaultUser(false)
                            .build())
                    .getId();
            columnRepository.saveAll(Arrays.asList(
                    new ColumnEntity(viewId, MANUFACTURER.getId() , 0, null, null),
                    new ColumnEntity(viewId, MODEL.getId(), 1, null, null),
                    new ColumnEntity(viewId, SERIAL.getId(), 2, null, null),
                    new ColumnEntity(viewId, CREATED.getId(), 3, null, null),
                    new ColumnEntity(viewId, UPDATED.getId(), 4, null, null),
                    new ColumnEntity(viewId, STATUS.getId(), 5, null, null)));

            wsSender.sendViewEvent(clientType, CREATE, getAbstractView(viewId, session));
        }

        if (viewType == ViewType.FrameView) {
            final List<Long> frameIds;

            frameIds = frameRepository.findAll()
                    .stream()
                    .map(FrameEntity::getId)
                    .collect(Collectors.toList());


            final Long mainViewId = viewRepository.saveAndFlush(ViewEntity.builder()
                            .name("Default")
                            .domainId(domainId)
                            .type(viewType)
                            .clientType(clientType)
                            .defaultDomain(true)
                            .defaultUser(false)
                            .tableWidth(3)
                            .build())
                    .getId();

            final List<ViewFrameEntity> frames = new ArrayList<>();
            for (int i = 0; i < frameIds.size(); i++) {
                frames.add(ViewFrameEntity.builder()
                        .frameId(frameIds.get(i))
                        .viewId(mainViewId)
                        .index(i)
                        .build());
            }
            viewFrameRepository.saveAll(frames);

            wsSender.sendViewEvent(clientType, CREATE, getFrameView(mainViewId));
        }
        return viewMapper.fieldsToViewSimples(viewRepository.getSimpleViews(clientType, viewType, domainId));
    }


    public List<ConditionItem> getItemsConditions(final String token) {
        final Session session = jwtService.getSession(token);
        final UserResponse userResponse = userService.getUserByIdWithoutDomain(session.getUserId(),
                session.getZoneId());
        final String locale = userResponse.getLocaleId() == null ? "EN" : userResponse.getLocaleId();

        return getConditionItems(locale, session.getZoneId(), userResponse.getDateFormat(), userResponse.getTimeFormat(), userResponse.getDomainId());
    }

    private List<ConditionItem>  getConditionItems(final String locale, final String zoneId, final String dateFormat, final String timeFormat, final Integer domainId) {
        List<ConditionItemEntity> items = domainId == null ?
                conditionItemRepository.findAll() :
                conditionItemRepository.findAllByDomainId(domainId);

        return items
                .stream()
                .map(this::setViewName)
                .map(viewMapper::conditionItemEntityToConditionItem)
                .map(item -> setFrameConditions(item, locale, zoneId, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    private ConditionItemEntity setViewName(ConditionItemEntity conditionItemEntity) {
        Optional<ViewEntity> view = viewRepository.findById(conditionItemEntity.getViewId());

        view.ifPresent(viewEntity -> conditionItemEntity.setViewName(viewEntity.getName()));
        return conditionItemEntity;
    }

    private ConditionItem setFrameConditions(final ConditionItem item, final String locale, final String zoneId,
                                             final String dateFormat, final String timeFormat) {
        return item.toBuilder()
                .conditions(getFrameConditions(item.getViewId(), null, locale, zoneId, dateFormat, timeFormat))
                .build();
    }

    private List<ViewCondition> getFrameConditions(final Long viewId, final Long parentId, final String locale, final String zoneId,
                                                   final String dateFormat, final String timeFormat) {
        final List<ViewCondition> viewConditions =
                frameConditionRepository.findAllByViewIdAndParentId(viewId, parentId)
                        .stream()
                        .map(condition
                                -> DeviceViewUtil.conditionFrameEntityToViewCondition(condition, locale, zoneId, dateFormat, timeFormat))
                        .collect(Collectors.toList());

        viewConditions.forEach(c -> c.setItems(getFrameConditions(viewId, c.getId(), locale, zoneId, dateFormat, timeFormat)));

        return viewConditions;
    }

    @Transactional
    public List<ConditionItem> createConditionItems(final String token, List<ConditionItem> conditionItems) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        List<ConditionItemEntity> allConditionItems = conditionItemRepository.findAll();

        List<Long> validDomains = new ArrayList<>();
        List<ConditionItem> domainsToCheck = getItemsConditions(token);
        domainsToCheck.addAll(conditionItems);
        checkItemsDomains(domainsToCheck, user, validDomains);

        List<Long> checkedIds = new ArrayList<>();
        allConditionItems.forEach(item -> checkIfItemExists(item, conditionItems, checkedIds, validDomains));
        conditionItems.forEach(item -> checkIfObjectIsNew(item, checkedIds, validDomains));

        return getItemsConditions(token);
    }

    private void checkItemsDomains(final List<ConditionItem> conditionItems,
                                                  final UserResponse user,
                                                  final List<Long> validDomains) {
        conditionItems.forEach(item ->
                isValidDomainForView(item, user.getDomainId(), validDomains));

    }

    private void isValidDomainForView(final ConditionItem item, Integer userDomainId,
                                         final List<Long> validDomains) {
        Optional<ViewEntity> viewOpt = viewRepository.findById(item.getViewId());

        if (viewOpt.isPresent()) {
            ViewEntity view = viewOpt.get();
            Integer viewDomainId = view.getDomainId();

            if (userDomainId.equals(viewDomainId)) {
                validDomains.add(item.getViewId());
            }
        }
    }


    private void checkIfObjectIsNew(ConditionItem item, List<Long> checkedIds, List<Long> validDomains) {
        if(!validDomains.contains(item.getViewId())) {
            checkedIds.add(item.getViewId());
            return;
        }
        if(!checkedIds.contains(item.getViewId()) && viewRepository.existsById(item.getViewId())) {
            saveOrUpdateConditionItem(item);
        }
    }

    private void checkIfItemExists(final ConditionItemEntity item,
                                   final List<ConditionItem> initialItems,
                                   final List<Long> checkedIds,
                                   final List<Long> validDomains) {
        if(!validDomains.contains(item.getViewId())) {
            checkedIds.add(item.getViewId());
            return;
        }
        if (initialItems.stream()
                .anyMatch(conditionItem -> conditionItem.getViewId().equals(item.getViewId()))) {
            findConditionItemByViewId(initialItems, item.getViewId())
                    .ifPresent(this::saveOrUpdateConditionItem);
            checkedIds.add(item.getViewId());
        } else {
            frameConditionRepository.deleteAllByViewId(item.getViewId());
            conditionItemRepository.delete(item);
        }
    }
    public static Optional<ConditionItem> findConditionItemByViewId(final List<ConditionItem> conditionItems, final Long viewId) {
        for (ConditionItem conditionItem : conditionItems) {
            if (conditionItem.getViewId().equals(viewId)) {
                return Optional.of(conditionItem);
            }
        }
        return Optional.empty();
    }


    private void saveOrUpdateConditionItem(final ConditionItem item) {
        Optional<ConditionItemEntity> entity = conditionItemRepository.findByViewId(item.getViewId());

        if(entity.isPresent()) {
            entity.get().setViewIndex(item.getViewIndex());
            conditionItemRepository.save(entity.get());
        } else {
            conditionItemRepository.save(DeviceViewUtil.conditionItemToEntity(item));
        }

        frameConditionRepository.deleteAllByViewId(item.getViewId());
        item.getConditions().forEach(condition -> saveFrameCondition(condition, item.getViewId(), null));
    }

    private void saveFrameCondition(final ViewCondition condition, final Long viewId, final Long parentId) {
        final FrameConditionEntity frameConditionEntity
                = frameConditionRepository.save(viewMapper.createFrameConditionEntity(condition, viewId, parentId));

        condition.getItems().forEach(item -> saveFrameCondition(item, viewId, frameConditionEntity.getId()));

    }
}
