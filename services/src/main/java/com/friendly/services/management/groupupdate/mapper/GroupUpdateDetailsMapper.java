package com.friendly.services.management.groupupdate.mapper;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.services.management.action.dto.enums.ActionOwnerTypeEnum;
import com.friendly.services.management.groupupdate.dto.GroupUpdateActivation;
import com.friendly.services.management.groupupdate.dto.GroupUpdateActivationPeriod;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateGroupChildDetails;
import com.friendly.services.management.groupupdate.dto.request.GroupUpdateGroupChildDetailsModify;
import com.friendly.services.management.groupupdate.dto.request.GroupUpdateGroupDetailsRequest;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateGroupDetailsResponse;
import com.friendly.services.management.groupupdate.dto.GroupUpdateReactivation;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateActivationType;
import com.friendly.services.management.groupupdate.dto.enums.SourceType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.management.action.service.ActionService;
import com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum;
import com.friendly.services.management.groupupdate.utils.UpdateGroupUtil;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupChildEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupPeriod;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupReactivate;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupDeviceRepository;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.friendly.services.infrastructure.utils.DateHelper;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.ftacs.IntegerArrayWS;
import com.ftacs.PeriodListWS;
import com.ftacs.PeriodWS;
import com.ftacs.ReactivateWS;
import com.ftacs.UpdateGroupChildListWS;
import com.ftacs.UpdateGroupChildWS;
import com.ftacs.UpdateGroupWS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupUpdateDetailsMapper {
    private final ActionService actionService;
    private final ProductClassGroupRepository productClassGroupRepository;
    private final UpdateGroupDeviceRepository updateGroupDeviceRepository;

    public GroupUpdateGroupDetailsResponse entityToDetails(
            UpdateGroupEntity updateGroupEntity,
            Session session,
            UserResponse user) {
        DateHelper dateHelper = DateHelper.builder()
                .session(session)
                .user(user)
                .build();

        return GroupUpdateGroupDetailsResponse.builder()
                .id(updateGroupEntity.getId())
                .name(updateGroupEntity.getName())
                .random(updateGroupEntity.getRandom())
                .configs(updateGroupEntity.getChildren()
                        .stream()
                        .map(this::childEntityToDetails)
                        .collect(Collectors.toList()))
                .activation(activationDetailsFromEntity(updateGroupEntity, dateHelper))
                .reactivation(updateGroupEntity.getReactivates().stream().findFirst()
                        .map(reactivateEntity -> reactivationEntityToDetails(reactivateEntity, dateHelper))
                        .orElse(null))
                .build();


    }

    public GroupUpdateGroupChildDetails childEntityToDetails(UpdateGroupChildEntity updateGroupChildEntity) {
        return GroupUpdateGroupChildDetails.builder()
                .conditionId(updateGroupChildEntity.getCustomViewId())
                .sourceType(updateGroupChildEntity.getCpeSourceType())
                .manufacturer(updateGroupChildEntity.getProductClass().getManufacturerName())
                .model(updateGroupChildEntity.getProductClass().getModel())
                .id(updateGroupChildEntity.getId())
                .tasks(actionService.getActionList(
                        updateGroupChildEntity.getProductClass().getId(),
                        updateGroupChildEntity.getId(),
                        ActionOwnerTypeEnum.UPDATE_GROUP))
                .build();
    }

    public GroupUpdateReactivation reactivationEntityToDetails(UpdateGroupReactivate reactivateEntity, DateHelper dateHelper) {
        return UpdateGroupUtil.cronExpressionToGroupUpdateReactivation(reactivateEntity.getExpression(),
                        GroupUpdateReactivation.builder())
                .startsOn(dateHelper.convertToZoneTime(reactivateEntity.getFromDate()))
                .reRunFailedDevices(reactivateEntity.getFailOnly())
                .reactivationCount(reactivateEntity.getRepeatCount())
                .endsReactivationDate(reactivateEntity.getToDate() == null ? null
                        : dateHelper.convertToZoneTime(reactivateEntity.getToDate()))
                .build();
    }

    public GroupUpdateActivation activationDetailsFromEntity(UpdateGroupEntity updateGroupEntity, DateHelper dateHelper) {
        return GroupUpdateActivation.builder()
                .type(updateGroupEntity.getScheduled() == null ?
                        GroupUpdateActivationType.Immediately :
                        GroupUpdateActivationType.Scheduled)
                .date(dateHelper.convertToZoneTime(updateGroupEntity.getScheduled()))
                .threshold(updateGroupEntity.getThreshold())
                .requestDeviceConnect(updateGroupEntity.getPush())
                .onlyOnlineDevices(updateGroupEntity.getOnlineCpeOnly())
                .stopOnFail(updateGroupEntity.getStopOnFail())
                .periodList(updateGroupEntity.getPeriods()
                        .stream()
                        .map(this::periodEntityToDetails)
                        .collect(Collectors.toList()))
                .build();
    }

    public GroupUpdateActivationPeriod periodEntityToDetails(UpdateGroupPeriod periodEntity) {
        Instant to = DateHelper.periodToInstant(periodEntity.getHourTo(), periodEntity.getMinuteTo());
        Instant from = DateHelper.periodToInstant(periodEntity.getHourFrom(), periodEntity.getMinuteFrom());

        return GroupUpdateActivationPeriod.builder()
                .to(DateTimeUtils.getDefaultDateTimeFormatter().format(to))
                .from(DateTimeUtils.getDefaultDateTimeFormatter().format(from))
                .devicesAmount(periodEntity.getAmount())
                .interval(periodEntity.getInterval())
                .build();
    }

    public UpdateGroupWS detailsToWS(GroupUpdateGroupDetailsRequest groupUpdateGroupDetails, UserResponse user, Session session) {
        UpdateGroupWS updateGroupWS = new UpdateGroupWS();
        updateGroupWS.setName(groupUpdateGroupDetails.getName());
        updateGroupWS.setLocationId(user.getDomainId());
        updateGroupWS.setPush(groupUpdateGroupDetails.getActivation().getRequestDeviceConnect());
        updateGroupWS.setOnlineOnly(groupUpdateGroupDetails.getActivation().getOnlyOnlineDevices());
        updateGroupWS.setRandomCnt(groupUpdateGroupDetails.getRandom());
        updateGroupWS.setStopOnFail(groupUpdateGroupDetails.getActivation().getStopOnFail());
        updateGroupWS.setThreshold(groupUpdateGroupDetails.getActivation().getThreshold());

        if (groupUpdateGroupDetails.getActivation().getType().equals(GroupUpdateActivationType.Scheduled)) {
            updateGroupWS.setScheduled(DateTimeUtils.convertIsoToServerXMLCalendar(
                    groupUpdateGroupDetails.getActivation().getDate(),
                    session.getClientType(), session.getZoneId()));
        }

        updateGroupWS.setPeriods(periodsToPeriodListWS(groupUpdateGroupDetails.getActivation().getPeriodList(), session));

        updateGroupWS.setReactivateOptions(CommonUtils.ACS_OBJECT_FACTORY.createUpdateGroupWSReactivateOptions(
                reactivationDetailsToWS(groupUpdateGroupDetails.getReactivation(), session)));

        updateGroupWS.setUpdateGroupChildList(childRequestToListWS(groupUpdateGroupDetails.getConfigs(), session));
        return updateGroupWS;
    }

    private UpdateGroupChildListWS childRequestToListWS(List<GroupUpdateGroupChildDetailsModify> configs, Session session) {
        UpdateGroupChildListWS updateGroupChildListWS = new UpdateGroupChildListWS();
        updateGroupChildListWS.getUpdateGroupChild().addAll(configs.stream()
                .map(child -> childDetailsToWS(child, session))
                .collect(Collectors.toList()));
        return updateGroupChildListWS;
    }

    private UpdateGroupChildWS childDetailsToWS(GroupUpdateGroupChildDetailsModify child, Session session) {
        Long groupId = productClassGroupRepository.findIdByManufacturerNameAndModel(
                        child.getManufacturer(),
                        child.getModel())
                .orElseThrow(() -> new FriendlyIllegalArgumentException(
                        ServicesErrorRegistryEnum.PRODUCT_CLASS_GROUP_NOT_FOUND,
                        child.getManufacturer(), child.getModel()));

        IntegerArrayWS arrWs = new IntegerArrayWS();

        if (child.getSourceType().equals(SourceType.Individual)) {
            Set<Integer> cpeIds = new HashSet<>(child.getSelectedDevices());
            if (child.getId() != null && child.getId() > 0) {
                cpeIds.addAll(updateGroupDeviceRepository.findCpeIdsByUpdateGroupId(child.getId())
                        .stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList()));
            }

            arrWs.getId().addAll(cpeIds);
            if (child.getUnselectedDevices() != null) {
                arrWs.getId().removeAll(child.getUnselectedDevices());
            }
        }

        UpdateGroupChildWS updateGroupChildWS = new UpdateGroupChildWS();
        updateGroupChildWS.setGroupId(groupId.intValue());
        updateGroupChildWS.setCustomViewId(child.getConditionId());
        updateGroupChildWS.setCpeIds(arrWs);
        updateGroupChildWS.setTasks(actionService.convertActionToUpdateGroupWSList(child.getTasks(), groupId));

        return updateGroupChildWS;
    }

    private ReactivateWS reactivationDetailsToWS(GroupUpdateReactivation reactivation, Session session) {
        ReactivateWS reactivateWS = new ReactivateWS();
        reactivateWS.setReactivateExpr(UpdateGroupUtil.reactivationDetailsToCron(
                reactivation,
                DateTimeUtils.clientToServer(
                        DateHelper.uiTimeToInstant(reactivation.getStartsOn(), session.getZoneId()),
                        session.getClientType(),
                        session.getZoneId())));

        reactivateWS.setReactivateOnFailed(reactivation.getReRunFailedDevices());
        reactivateWS.setReactivateRepeats(reactivation.getReactivationCount());
        reactivateWS.setFrom(DateTimeUtils.convertIsoToServerXMLCalendar(
                reactivation.getStartsOn(),
                session.getClientType(), session.getZoneId()));
        if (StringUtils.hasText(reactivation.getEndsReactivationDate())) {
            reactivateWS.setTo(DateTimeUtils.convertIsoToServerXMLCalendar(
                    reactivation.getEndsReactivationDate(),
                    session.getClientType(), session.getZoneId()));
        }
        return reactivateWS;
    }

    private PeriodListWS periodsToPeriodListWS(List<GroupUpdateActivationPeriod> periodList, Session session) {
        if (periodList.isEmpty()) {
            return null;
        }
        PeriodListWS periodListWS = new PeriodListWS();
        periodListWS.getPeriod().addAll(periodList.stream()
                .map(p -> periodToPeriodWS(p, session))
                .collect(Collectors.toList()));

        return periodListWS;
    }

    private PeriodWS periodToPeriodWS(GroupUpdateActivationPeriod period, Session session) {
        PeriodWS periodWS = new PeriodWS();
        periodWS.setAmount(period.getDevicesAmount());
        periodWS.setInterval(period.getInterval());

        Instant from = DateHelper.uiTimeToInstant(period.getFrom(), session.getZoneId());
        Instant to = DateHelper.uiTimeToInstant(period.getTo(), session.getZoneId());

        periodWS.setHourFrom(from.get(ChronoField.HOUR_OF_DAY));
        periodWS.setHourTo(to.get(ChronoField.HOUR_OF_DAY));

        periodWS.setMinuteFrom(from.get(ChronoField.MINUTE_OF_HOUR));
        periodWS.setMinuteTo(to.get(ChronoField.MINUTE_OF_HOUR));
        return periodWS;
    }


}