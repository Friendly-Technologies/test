package com.friendly.services.management.groupupdate.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.management.groupupdate.dto.enums.ActivationMethod;
import com.friendly.services.management.groupupdate.dto.enums.ActivationType;
import com.friendly.services.management.groupupdate.dto.GroupUpdateCondition;
import com.friendly.services.management.groupupdate.dto.GroupUpdateDevice;
import com.friendly.services.management.groupupdate.dto.GroupUpdateGroup;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateReport;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateSerialResponse;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.DeviceStateProjection;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewEntity;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupTransactionRepository;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.ActivityLogUpdatesRepository;
import com.friendly.services.infrastructure.utils.DateTimeUtils;

import java.time.Instant;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.friendly.services.management.groupupdate.dto.enums.ActivationMethod.ActivatedManual;
import static com.friendly.services.management.groupupdate.dto.enums.ActivationMethod.ActivatedScheduled;
import static com.friendly.services.management.groupupdate.dto.enums.ActivationMethod.NotActivated;
import static com.friendly.services.management.groupupdate.dto.enums.ActivationType.ScheduledActivate;
import static com.friendly.services.management.groupupdate.dto.enums.ActivationType.UpdateActivate;
import static com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType.SCHEDULED;

@Component
@RequiredArgsConstructor
public class GroupUpdateMapper {

    @NonNull
    private ActivityLogUpdatesRepository activityLogUpdatesRepository;
    @NonNull
    private UpdateGroupTransactionRepository updateGroupTransactionRepository;

    public GroupUpdateReport entityToGroupUpdateReport(final UpdateGroupEntity entity, final ClientType clientType,
                                                       final String zoneId, final String dateFormat,
                                                       final String timeFormat) {
        final GroupUpdateReport.GroupUpdateReportBuilder builder =
                GroupUpdateReport.builder()
                                 .id(entity.getId())
                                 .domain(entity.getDomain())
                                 .name(entity.getName())
                                 .creator(entity.getCreator())
                                 .state(entity.getState())
                                 .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                                 .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                                 .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                                                                  dateFormat, timeFormat))
                                 .updated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType, zoneId,
                                                                  dateFormat, timeFormat))
                                 .groups(entity.getChildren()
                                               .stream()
                                               .map(c -> {
                                                   final ProductClassGroupEntity productClass = c.getProductClass();
                                                   return GroupUpdateDevice.builder()
                                                                           .id(productClass.getId().intValue())
                                                                           .manufacturer(
                                                                                   productClass.getManufacturerName())
                                                                           .model(productClass.getModel())
                                                                           .build();
                                               })
                                               .collect(Collectors.toList()));
        if (entity.getScheduled() != null) {
            if (entity.getState().equals(SCHEDULED)) {
                builder.activatedIso(DateTimeUtils.serverToClient(entity.getScheduled(), clientType, zoneId))
                       .activated("Scheduled to " + DateTimeUtils.formatAcs(entity.getScheduled(), clientType,
                                                                            zoneId, dateFormat, timeFormat));
            } else {
                builder.activatedIso(DateTimeUtils.serverToClient(entity.getScheduled(), clientType, zoneId))
                       .activated(DateTimeUtils.formatAcs(entity.getScheduled(), clientType,
                                                          zoneId, dateFormat, timeFormat));
            }
        } else if (entity.getState().equals(GroupUpdateStateType.COMPLETED)) {
            builder.activatedIso(DateTimeUtils.serverToClient(entity.getUpdated(), clientType, zoneId))
                   .activated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType,
                                                      zoneId, dateFormat, timeFormat));
        }

        return builder.build();
    }

    public GroupUpdateGroup entityToGroupUpdateGroup(final UpdateGroupEntity entity, final ClientType clientType,
                                                     final String zoneId, final String dateFormat,
                                                     final String timeFormat) {
        GroupUpdateGroup groups =
                GroupUpdateGroup.builder()
                        .id(entity.getId())
                        .domain(entity.getDomain())
                        .name(entity.getName())
                        .creator(entity.getCreator())
                        .state(entity.getState())
                        .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                        .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                        .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                                dateFormat, timeFormat))
                        .updated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType, zoneId,
                                dateFormat, timeFormat))
                        .activationMethod(getActivationMethod(entity.getId(), entity.getState()))
                        .application(clientType == ClientType.mc ? "MP" : "SP")
                        .build();

        Instant activatedDate = DbConfig.isOracle()
                ? updateGroupTransactionRepository.getActivatedDateOracle(entity.getId()).toInstant()
                : updateGroupTransactionRepository.getActivatedDate(entity.getId());
            if (activatedDate != null) {
              groups.setActivatedIso(DateTimeUtils.serverToUtc(activatedDate, clientType));
              groups.setActivated(
                  DateTimeUtils.formatAcs(activatedDate, clientType, zoneId, dateFormat, timeFormat));
        }
        return groups;
    }

    private ActivationMethod getActivationMethod(Integer updateId, GroupUpdateStateType state) {
        switch (state) {
            case NOT_ACTIVE:
                return NotActivated;
            case SCHEDULED_FOR_REACTIVATION:
            case SCHEDULED:
                return ActivatedScheduled;
            default:
                ActivationType type = activityLogUpdatesRepository.getActivationMethod(updateId, UpdateActivate.name(), ScheduledActivate.name());
                if (type == null) {
                    return NotActivated;
                }
                return type.equals(UpdateActivate) ? ActivatedManual : ActivatedScheduled;
        }
    }

    public static GroupUpdateCondition viewEntityToGroupUpdateCondition(ViewEntity view) {
        return GroupUpdateCondition.builder()
                .id(view.getId())
                .name(view.getName())
                .build();
    }

    public GroupUpdateSerialResponse entityToGroupUpdateSerial(Object[] device) {
        return GroupUpdateSerialResponse.builder()
                .id((Long) device[0])
                .serial((String) device[1])
                .build();
    }

    public GroupUpdateSerialResponse deviceStateEntityToResponse(DeviceStateProjection entity) {
        return GroupUpdateSerialResponse.builder()
                .id(entity.getId())
                .serial(entity.getSerial())
                .selected(entity.getSelected() != null && entity.getSelected() > 0)
                .state(entity.getState() == null ? null : entity.getState())
                .build();
    }
}
