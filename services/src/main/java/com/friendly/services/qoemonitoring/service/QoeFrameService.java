package com.friendly.services.qoemonitoring.service;

import static com.friendly.commons.models.device.frame.ConditionType.Equal;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FRAME_NOT_UNIQUE;
import static com.friendly.services.device.info.utils.helper.QueryViewHelper.getPredicateFromConditions;
import static com.friendly.services.infrastructure.utils.DateTimeUtils.getInstantForClient;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.frame.ConditionFilter;
import com.friendly.commons.models.device.frame.GetQoeDetailsRequest;
import com.friendly.commons.models.device.frame.KpiData;
import com.friendly.commons.models.device.frame.response.GetQoeDetailsResponse;
import com.friendly.commons.models.device.response.GetParametersFullNames;
import com.friendly.commons.models.device.response.QoeFrameItem;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.request.LongIdsRequest;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagSpeedTestEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.KpiDataEntity;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.qoemonitoring.mapper.QoeFrameMapper;
import com.friendly.services.device.info.orm.acs.model.CpeEntity;
import com.friendly.services.qoemonitoring.orm.acs.model.KpiEntity;
import com.friendly.services.qoemonitoring.orm.iotw.model.QoeFrameItemEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.qoemonitoring.orm.acs.repository.KpiRepository;
import com.friendly.services.qoemonitoring.orm.iotw.repository.QoeFrameItemRepository;
import com.friendly.services.uiservices.view.orm.iotw.repository.ViewFrameRepository;
import com.friendly.services.qoemonitoring.orm.qoe.repository.KpiDataRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QoeFrameService {
    @NonNull
    private final KpiDataRepository kpiDataRepository;

    @NonNull
    private final ViewFrameRepository viewFrameRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final QoeFrameItemRepository qoeFrameItemRepository;

    @NonNull
    private final KpiRepository kpiRepository;

    @NonNull
    private final QoeFrameMapper qoeFrameMapper;
    private final CpeRepository cpeRepository;

    public QoeFrameItem createQoeFrameItem(final String token, final QoeFrameItem item) {
        jwtService.getSession(token);

        Optional<QoeFrameItemEntity> optionalFrame = Optional.empty();
        if(item.getId() != null) {
            optionalFrame = qoeFrameItemRepository.findById(item.getId());
        } else if(qoeFrameItemRepository.findByName(item.getName()).isPresent()) {
            throw new FriendlyEntityNotFoundException(FRAME_NOT_UNIQUE, item.getName());
        }
        final Long nameId = kpiRepository.findByKpiName(item.getParameterName()).getId();

        return qoeFrameMapper.qoeFrameEntityToObject(
                qoeFrameItemRepository.save(
                        qoeFrameMapper.toQoeFrameItemEntity(
                                item,
                                nameId,
                                optionalFrame.map(QoeFrameItemEntity::getId).orElse(null)
                        )
                ), item.getParameterName()
        );
    }

    public List<QoeFrameItem> getQoeFrameItem(final String token, final LongIdsRequest request) {
        jwtService.getSession(token);

        return request.getIds()
                .stream()
                .map(qoeFrameItemRepository::findById)
                .filter(Optional::isPresent)
                .map(entity -> qoeFrameMapper.qoeFrameEntityToObject(entity.get(), null))
                .collect(Collectors.toList());
    }

    public GetParametersFullNames getParametersFullNames(final String token) {
        jwtService.getSession(token);

        return new GetParametersFullNames(kpiRepository.getFullNames());
    }


    @Transactional
    public void deleteQoeFrameItem(String token, LongIdRequest request) {
        jwtService.getSession(token);

        viewFrameRepository.deleteAllByFrameId(request.getId());
        qoeFrameItemRepository.deleteById(request.getId());
    }

    public GetQoeDetailsResponse getQoeFrameItemDetails(String token, GetQoeDetailsRequest request) {
        Session session = jwtService.getSession(token);

        Optional<QoeFrameItemEntity> qoeFrame = qoeFrameItemRepository.findById(request.getFrameId());
        Optional<CpeEntity> cpe = cpeRepository.findById(request.getDeviceId());
        if(!qoeFrame.isPresent() || !cpe.isPresent()) {
            return new GetQoeDetailsResponse();
        }
        KpiEntity kpiEntity = kpiRepository.findByKpiName(qoeFrame.get().getParameterName());
        Long kpiId = kpiEntity.getId();
        String serial = cpe.get().getSerial();

        Specification<KpiDataEntity> specification =
                getListFilers(getConditions(request.getConditions(), kpiId, serial), session.getClientType(), session.getZoneId());

        try {
      return new GetQoeDetailsResponse(
          kpiDataRepository.findAll(specification).stream()
              .map(entity -> qoeFrameMapper.kpiDataEntityToObject(entity, session.getClientType(), session.getZoneId()))
              .sorted(Comparator.comparing(KpiData::getCreatedIso))
              .collect(Collectors.toList()));
        } catch (CannotCreateTransactionException ex) {
//            throw new FriendlyPermissionException(DATABASE_NOT_FOUND, "clickhouse");
            return new GetQoeDetailsResponse(Collections.emptyList());
        }
    }

    private List<ConditionFilter> getConditions(final ConditionFilter condition,
                                                final Long kpiId, final String serial) {
        return Arrays.asList(
                condition,
                ConditionFilter.builder()
                        .compare(Equal)
                        .conditionString(String.valueOf(kpiId))
                        .build(),
                ConditionFilter.builder()
                        .compare(Equal)
                        .conditionString(String.valueOf(serial))
                        .build()
        );
    }

    public Specification<KpiDataEntity> getListFilers(final List<ConditionFilter> frameConditions,
                                                      final ClientType clientType, final String zoneId) {

        return (root, cq, cb) -> getFilter(root, cb, frameConditions, ConditionLogic.And, cb.and(), clientType, zoneId);
    }


    public Predicate getFilter(final Root<KpiDataEntity> root,
                               final CriteriaBuilder cb,
                               final List<ConditionFilter> conditions,
                               final ConditionLogic logic,
                               final Predicate parentPredicate,
                               final ClientType clientType,
                               final String zoneId) {
        final List<Predicate> predicates = conditions.stream()
                .map(c ->  cb.and(getPredicateFromCondition(root, cb, c, clientType, zoneId)))
                .collect(Collectors.toList());
        return getPredicateFromConditions(cb, logic, parentPredicate, predicates);
    }

    public static Predicate getPredicateFromCondition(final Root<KpiDataEntity> root,
                                                      final CriteriaBuilder cb,
                                                      final ConditionFilter condition,
                                                      final ClientType clientType,
                                                      final String zoneId) {
        Predicate result;
        Path<Object> path = root.get("created");
        if(condition.getCompare().equals(Equal)) {
            if(isNumeric(condition.getConditionString())) {
                path = root.get("kpiId");
            } else {
                path = root.get("serial");
            }
        }
        result = getPredicateFromCondition(
               path, cb, condition, clientType, zoneId);
        return result;
    }

    public static <E> Predicate getPredicateFromCondition(final Path<E> path, final CriteriaBuilder cb,
                                                          final ConditionFilter condition, final ClientType clientType,
                                                          final String zoneId) {
        final String stringValue = condition.getConditionString();
        final Instant dateValue = condition.getConditionDateIso();
        final Instant toDateValue = condition.getConditionToDateIso();
        final Instant fromDateValue = condition.getConditionFromDateIso();
        final Instant now = Instant.now();
        Predicate result;

        switch (condition.getCompare()) {
            case Today:
                result = cb.greaterThan(path.as(Instant.class),
                         DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now.truncatedTo(ChronoUnit.DAYS), clientType, zoneId)));
                break;
            case Before:
                result = cb.lessThan(path.as(Instant.class),
                                DateTimeUtils.clientToServer(dateValue, clientType, zoneId));
                break;
            case Later:
                result = cb.greaterThan(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(DateTimeUtils.convertIsoDateToServer(dateValue), clientType, zoneId)));
                break;
            case BeforeToday:
                result = cb.lessThan(path.as(Instant.class),
                        getInstantForClient(now.truncatedTo(ChronoUnit.DAYS), zoneId));
                break;
            case Yesterday:
                Instant from =
                    getInstantForClient(now.minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS), zoneId);
                Instant to = getInstantForClient(now.truncatedTo(ChronoUnit.DAYS), zoneId);
                result = cb.between(path.as(Instant.class), from, to);
                break;
            case Prev7Days:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now.minus(7, ChronoUnit.DAYS)
                                .truncatedTo(ChronoUnit.DAYS), clientType, zoneId)),
                                DateTimeUtils.convertServerDateToIso(
                                        DateTimeUtils.clientToServer(now, clientType, zoneId)));
                break;
            case PrevXDays:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now.minus(Integer.parseInt(stringValue), ChronoUnit.DAYS)
                                .truncatedTo(ChronoUnit.DAYS), clientType, zoneId)),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now, clientType, zoneId)));
                break;
            case Prev3Hours:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now.minus(3, ChronoUnit.HOURS), clientType, zoneId)),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now, clientType, zoneId)));
                break;
            case PrevXHours:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now.minus(Integer.parseInt(stringValue), ChronoUnit.HOURS),
                                        clientType, zoneId)),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(now, clientType, zoneId)));
                break;
            case Between:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(DateTimeUtils.convertIsoDateToServer(fromDateValue), clientType, zoneId)),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(DateTimeUtils.convertIsoDateToServer(toDateValue), clientType, zoneId)));
                break;
            case OnDay:
                result = cb.greaterThan(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(dateValue.truncatedTo(ChronoUnit.DAYS), clientType, zoneId)));
                break;
            default:
                result = cb.equal(path, stringValue);
                break;
        }
        return result;
    }

    public static boolean isNumeric(String str) {
        String numericRegex = "-?\\d+(\\.\\d+)?";

        Pattern pattern = Pattern.compile(numericRegex);
        return pattern.matcher(str).matches();
    }

    public Specification<DiagSpeedTestEntity> getListFilersForSpeedTest(List<ConditionFilter> frameConditions, ClientType clientType, String zoneId) {
        return (root, cq, cb) -> getFilterSpeedTest(root, cb, frameConditions, ConditionLogic.And, cb.and(), clientType, zoneId);
    }

    private Predicate getFilterSpeedTest(Root<DiagSpeedTestEntity> root, CriteriaBuilder cb, List<ConditionFilter> conditions, ConditionLogic logic, Predicate parentPredicate, ClientType clientType, String zoneId) {
        final List<Predicate> predicates = conditions.stream()
                .map(c ->  cb.and(getPredicateFromConditionSpeedTest(root, cb, c, clientType, zoneId)))
                .collect(Collectors.toList());
        return getPredicateFromConditions(cb, logic, parentPredicate, predicates);
    }

    public static Predicate getPredicateFromConditionSpeedTest(final Root<DiagSpeedTestEntity> root,
                                                      final CriteriaBuilder cb,
                                                      final ConditionFilter condition,
                                                               final ClientType clientType,
                                                               final String zoneId) {
        Predicate result;
        result = getPredicateFromCondition(
                condition.getCompare().equals(Equal) ? root.get("serial") :
                        root.get("created"),
                cb, condition, clientType, zoneId);
        return result;
    }
}
