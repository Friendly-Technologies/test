package com.friendly.services.qoemonitoring.service;

import static com.friendly.services.device.info.utils.helper.QueryViewHelper.getPredicateFromConditions;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.UserExperienceConditionFilter;
import com.friendly.commons.models.device.UserExperienceConditionType;
import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserExperienceUtilFilterService<T> {


    public Specification<T> getListFilters(final List<UserExperienceConditionFilter> conditions,
                                           final ClientType clientType,
                                           final String zoneId) {
        return (root, cq, cb) -> getUserExperienceFilter(root, cb, conditions, ConditionLogic.And, cb.and(), clientType, zoneId);
    }

    public Predicate getUserExperienceFilter(final Root<T> root,
                                             final CriteriaBuilder cb,
                                             final List<UserExperienceConditionFilter> conditions,
                                             final ConditionLogic logic,
                                             final Predicate parentPredicate,
                                             final ClientType clientType,
                                             final String zoneId) {
        final List<Predicate> predicates = conditions.stream()
                .map(c ->  cb.and(getPredicateFromConditionForWifiEvents(root, cb, c, clientType, zoneId)))
                .collect(Collectors.toList());
        return getPredicateFromConditions(cb, logic, parentPredicate, predicates);
    }

    private Predicate getPredicateFromConditionForWifiEvents(final Root<T> root,
                                                             final CriteriaBuilder cb,
                                                             final UserExperienceConditionFilter condition,
                                                             final ClientType clientType,
                                                             final String zoneId) {
        Predicate result;
        result = getPredicateFromCondition(
                condition.getCompare().equals(UserExperienceConditionType.Equal) ?
                        root.get("serial") : root.get("created"),
                cb, condition, clientType, zoneId);
        return result;
    }

    public static <E> Predicate getPredicateFromCondition(final Path<E> path, final CriteriaBuilder cb,
                                                          final UserExperienceConditionFilter condition,
                                                          final ClientType clientType, final String zoneId) {
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
            case Between:
                result = cb.between(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(DateTimeUtils.convertIsoDateToServer(fromDateValue), clientType, zoneId)),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(DateTimeUtils.convertIsoDateToServer(toDateValue), clientType, zoneId)));
                break;
            case Before:
                result = cb.lessThan(path.as(Instant.class),
                        DateTimeUtils.clientToServer(dateValue, clientType, zoneId));
                break;
            case Later:
                result = cb.greaterThan(path.as(Instant.class),
                        DateTimeUtils.convertServerDateToIso(
                                DateTimeUtils.clientToServer(DateTimeUtils.convertIsoDateToServer(dateValue), clientType, zoneId)));;
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

}
