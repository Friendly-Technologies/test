package com.friendly.services.device.info.utils.helper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AccountInfoRequest;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.services.device.info.orm.acs.model.CustomDeviceEntity;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.device.info.utils.DeviceViewUtil;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnConditionEntity;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.friendly.commons.models.view.ConditionLogic.And;
import static com.friendly.services.device.info.utils.DeviceViewUtil.isCustomDeviceParam;
import static com.friendly.services.device.info.utils.DeviceViewUtil.isParameterSearch;
import static javax.persistence.criteria.Predicate.BooleanOperator.AND;

public class QueryViewHelper {

    private QueryViewHelper () {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<DeviceEntity> getSearchFilters(final List<Integer> domainIds, final AccountInfoRequest request) {
        return (root, cq, cb) -> {
            Integer domainId = request.getDomainId();
            Predicate domainPredicate = domainIds == null ? null :
                    cb.in(root.get("domainId")).value(domainIds);

            List<Predicate> orPredicates = new ArrayList<>();

            if (domainId != null && domainIds != null) {
                domainPredicate = cb.and(domainPredicate, (DeviceViewUtil.getPredicateFromSearchParam(root, cb, "domainId",
                        String.valueOf(request.getDomainId()), true)));
            } else if (domainId != null) {
                domainPredicate = (DeviceViewUtil.getPredicateFromSearchParam(root, cb, "domainId",
                        String.valueOf(request.getDomainId()), true));
            }

            if (request.getUserLogin() != null) {
                orPredicates.add(DeviceViewUtil.getPredicateFromSearchParam(root, cb, "userLogin",
                        request.getUserLogin(), true));
            }

            if (request.getPhone() != null) {
                orPredicates.add(DeviceViewUtil.getPredicateFromSearchParam(root, cb, "phone",
                        request.getPhone(), true));
            }

            Predicate orPred = cb.or(orPredicates.toArray(new Predicate[0]));
            return domainId == null ? cb.or(orPred) : cb.and(domainPredicate, orPred);
        };
    }

    public static Specification<DeviceEntity> getSearchFilters(final List<Integer> domainIds, final Integer protocolId,
                                                         final Long exceptDeviceId, final String searchColumn,
                                                         final String searchParam, final Boolean searchExact) {
        return (root, cq, cb) -> {
//            root.fetch("productClass");
//            root.fetch("customDevice");
            final Predicate domainPredicate = domainIds == null ? null :
                    cb.in(root.get("domainId")).value(domainIds);
            final Predicate protocolPredicate = protocolId == null ? null :
                    cb.equal(root.get("protocolId"), protocolId);
            final Predicate exceptDeviceIdPredicate = exceptDeviceId == null ? null :
                    cb.notEqual(root.get("id"), exceptDeviceId);
//            cq.distinct(true);
            return cb.and(Stream.of(domainPredicate, protocolPredicate, exceptDeviceIdPredicate,
                            DeviceViewUtil.getPredicateFromSearchParam(root, cb, searchColumn,
                                    searchParam, searchExact))
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new));
        };
    }

    public static Predicate getPredicate(List<Integer> domainIds, String manufacturer, String model, ProtocolType protocolType, Long exceptDeviceId, Root<DeviceEntity> root, CriteriaBuilder cb) {
        final Predicate domainPredicate = domainIds == null
                ? null : domainIds.contains(0)
                ? cb.or(cb.in(root.get("domainId")).value(domainIds),
                cb.isNull(root.get("domainId"))) : cb.in(root.get("domainId")).value(domainIds);

        final Predicate manufacturerPredicate = manufacturer == null ? null :
                cb.equal(root.get("productClass").get("productGroup").get("manufacturerName"), manufacturer);
        final Predicate modelPredicate = model == null ? null :
                cb.equal(root.get("productClass").get("productGroup").get("model"), model);
        final Predicate protocolPredicate = protocolType == null ? null :
                cb.equal(root.get("protocolId"), DeviceUtils.convertProtocolTypeToId(protocolType));
        final Predicate exceptDeviceIdPredicate = exceptDeviceId == null ? null :
                cb.notEqual(root.get("id"), exceptDeviceId);

        return cb.and(Stream.of(domainPredicate, protocolPredicate, exceptDeviceIdPredicate, modelPredicate, manufacturerPredicate)
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new));
    }

    public static Predicate getPredicate(List<Integer> domainIds, ProtocolType protocolType, Long exceptDeviceId,
                                          Root<DeviceEntity> root, CriteriaBuilder cb) {
        final Predicate domainPredicate = domainIds == null ? null : cb.in(root.get("domainId")).value(domainIds);

        final Predicate protocolPredicate = protocolType == null ? null :
                cb.equal(root.get("protocolId"), DeviceUtils.convertProtocolTypeToId(protocolType));
        final Predicate exceptDeviceIdPredicate = exceptDeviceId == null ? null :
                cb.notEqual(root.get("id"), exceptDeviceId);

        return cb.and(Stream.of(domainPredicate, protocolPredicate, exceptDeviceIdPredicate)
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new));
    }


    public static Predicate getPredicateFromConditions(CriteriaBuilder cb, ConditionLogic logic, Predicate parentPredicate, List<Predicate> predicates) {
        if (predicates.isEmpty()) {
            return parentPredicate;
        }
        Predicate predicate;
        if (logic.equals(And)) {
            predicate = cb.and(predicates.stream()
                    .map(p -> p.getOperator().equals(AND) ? cb.and(parentPredicate, p) : cb.or(parentPredicate, p))
                    .toArray(Predicate[]::new));
        } else {
            predicate = cb.or(predicates.stream()
                    .map(p -> p.getOperator().equals(AND) ? cb.and(parentPredicate, p) : cb.or(parentPredicate, p))
                    .toArray(Predicate[]::new));
        }
        return predicate;
    }

    public static <E> Predicate getPredicateFromCondition(final Path<E> path, final CriteriaBuilder cb,
                                                          final ColumnConditionEntity condition,
                                                          final ClientType clientType, final String zoneId,
                                                          final Root<DeviceEntity> root, final CriteriaQuery<?> cq) {
        final String columnKey = condition.getColumnKey();
        final String stringValue = condition.getStringValue();
        final Instant dateValue = condition.getDateValue();
        final Instant now = Instant.now();
        final Instant client = DateTimeUtils.addZoneId(now, zoneId);
        Predicate result;

        switch (condition.getType()) {
            case Equal:
                if (isParameterSearch(columnKey)) {
                    result = cb.or(
                            cb.equal(path, stringValue.toLowerCase()),
                            cb.equal(path, stringValue.toUpperCase())
                    );
                } else if ("protocolType".equals(columnKey)) {
                    result = cb.equal(path, DeviceUtils.convertProtocolTypeToId(ProtocolType.valueOf(stringValue)));
                } else {
                    result = cb.equal(path, stringValue.toLowerCase());
                }
                break;
            case NotEqual:
                if (isParameterSearch(columnKey)) {
                    result = cb.or(
                            cb.equal(path, stringValue.toLowerCase()),
                            cb.equal(path, stringValue.toUpperCase())
                    );
                } else if ("protocolType".equals(columnKey)) {
                    result = cb.notEqual(path, DeviceUtils.convertProtocolTypeToId(ProtocolType.valueOf(stringValue)));
                }  else if(isCustomDeviceParam(columnKey)){
                    Join<DeviceEntity, CustomDeviceEntity> customDeviceJoin
                            = root.join("customDevice", JoinType.LEFT);
                    result = cb.or(
                            cb.isNull(path),
                            cb.notEqual(path, stringValue),
                            cb.isNull(customDeviceJoin.get("serial"))
                    );

                } else {
                    result = cb.or(
                            cb.notEqual(path, stringValue),
                            cb.isNull(path)
                    );
                }
                break;
            case Like:
                if (isParameterSearch(columnKey)) {
                    result = cb.like(path.as(String.class),
                            "%" + stringValue.toLowerCase() + "%");
                } else {
                    result = cb.like(path.as(String.class),
                            "%" + stringValue + "%");
                }
                break;
            case NotLike:
                if (isParameterSearch(columnKey)) {
                    result = cb.like(path.as(String.class),
                            "%" + stringValue.toLowerCase() + "%");
                } else {
                    result = cb.or(
                            cb.notLike(path.as(String.class), "%" + stringValue + "%"),
                            cb.isNull(path.as(String.class)));
                }
                break;
            case StartsWith:
                if (isParameterSearch(columnKey)) {
                    result = cb.or(
                            cb.like(path.as(String.class), stringValue.toLowerCase() + "%"),
                            cb.like(path.as(String.class), stringValue.toUpperCase() + "%")
                    );
                } else {
                    result = cb.like(path.as(String.class), stringValue + "%");
                }
                break;
            case InList:
                result = path.in(Arrays.asList(splitAndJoinInListFilter(stringValue)));
                break;
            case IsNull:
                if (isParameterSearch(columnKey)) {
                    result = cb.and(cb.isNotNull(path), cb.notEqual(path.as(String.class), ""));
                } else if(isCustomDeviceParam(columnKey)){
                    Join<DeviceEntity, CustomDeviceEntity> customDeviceJoin
                            = root.join("customDevice", JoinType.LEFT);
                    return cb.or(
                            cb.isNull(path),
                            cb.equal(path.as(String.class), ""),
                            cb.isNull(customDeviceJoin.get("serial"))
                    );

                } else {
                    return cb.or(
                            cb.isNull(path),
                            cb.equal(path.as(String.class), "")
                    );
                }
                break;
            case IsNotNull:
                result = cb.and(cb.isNotNull(path), cb.notEqual(path.as(String.class), ""));
                break;
            case OnDay:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.clientToServer(dateValue, clientType, zoneId),
                        DateTimeUtils.clientToServer(dateValue.plus(1, ChronoUnit.DAYS), clientType, zoneId));
                break;
            case PriorTo:
                result = cb.lessThan(path.as(Instant.class),
                        DateTimeUtils.clientToServer(dateValue, clientType, zoneId));
                break;
            case LaterThan:
                result = cb.greaterThan(path.as(Instant.class),
                        DateTimeUtils.clientToServer(dateValue, clientType, zoneId));
                break;
            case Today:
                result = cb.greaterThan(path.as(Instant.class),
                        DateTimeUtils.clientToServer(now.truncatedTo(ChronoUnit.DAYS), clientType, zoneId));
                break;
            case BeforeToday:
                result = cb.lessThan(path.as(Instant.class),
                        DateTimeUtils.clientToServer(now.truncatedTo(ChronoUnit.DAYS), clientType, zoneId));
                break;
            case Yesterday:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.clientToServer(now.minus(1, ChronoUnit.DAYS)
                                .truncatedTo(ChronoUnit.DAYS), clientType, zoneId),
                        DateTimeUtils.clientToServer(now.truncatedTo(ChronoUnit.DAYS), clientType, zoneId));
                break;
            case Prev7Days:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.clientToServer(now.minus(7, ChronoUnit.DAYS)
                                .truncatedTo(ChronoUnit.DAYS), clientType, zoneId),
                        DateTimeUtils.clientToServer(client, clientType, zoneId));
                break;
            case PrevXDays:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.clientToServer(now.minus(Integer.parseInt(stringValue), ChronoUnit.DAYS)
                                .truncatedTo(ChronoUnit.DAYS), clientType, zoneId),
                        DateTimeUtils.clientToServer(client, clientType, zoneId));
                break;
            case On:
                result = cb.isTrue(path.as(Boolean.class));
                break;
            case Off:
                result = cb.isTrue(path.as(Boolean.class)).not();
                break;
            default:
                result = cb.equal(path, stringValue);
                break;
        }
        return result;
    }

    private static String[] splitAndJoinInListFilter(String input) {
        input = input.replaceAll("\\s+", "");
        return input.split(",");
    }

    public static Join<DeviceEntity, ?> resolveJoin(Root<DeviceEntity> root) {
        return root.getJoins() == null
                ? root.join("cpeParameters", JoinType.INNER)
                : root.getJoins().stream()
                .filter(deviceEntityJoin -> deviceEntityJoin.getAttribute().getName().equals("cpeParameters"))
                .findFirst()
                .orElseGet(() -> root.join("cpeParameters", JoinType.INNER));
    }
}