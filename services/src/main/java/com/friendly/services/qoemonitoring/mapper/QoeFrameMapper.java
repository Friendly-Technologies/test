package com.friendly.services.qoemonitoring.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.frame.KpiData;
import com.friendly.commons.models.device.frame.SpeedTest;
import com.friendly.commons.models.device.response.QoeFrameItem;
import com.friendly.commons.models.view.FrameSimple;
import com.friendly.commons.models.view.PropertyType;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagSpeedTestEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.KpiDataEntity;
import com.friendly.services.qoemonitoring.orm.iotw.model.QoeFrameItemEntity;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class QoeFrameMapper {

    public QoeFrameItem qoeFrameEntityToObject(QoeFrameItemEntity e, String parameterName) {
        return QoeFrameItem.builder()
                .id(e.getId())
                .name(e.getName())
                .parameterName(e.getParameterName() == null ? parameterName : e.getParameterName())
                .protocol(e.getProtocol())
                .height(e.getHeight())
                .sort(e.getSortDir())
                .mode(e.getMode())
                .setDefault(e.getSortParam())
                .days(e.getPeriod())
                .build();
    }

    public QoeFrameItemEntity toQoeFrameItemEntity(final QoeFrameItem item, final Long nameId, final Long id) {
        return QoeFrameItemEntity.builder()
                .id(id)
                .nameId(nameId.intValue())
                .name(item.getName())
                .protocol(item.getProtocol())
                .height(item.getHeight())
                .sortDir(item.getSort())
                .sortParam(item.getSetDefault())
                .mode(item.getMode())
                .period(item.getDays())
                .build();
    }

    public FrameSimple qoeFrameToFrameSimple(final QoeFrameItemEntity entity) {
        return FrameSimple.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parameterName(entity.getParameterName())
                .type(PropertyType.QOE)
                .build();
    }


    public KpiData kpiDataEntityToObject(final KpiDataEntity entity, final ClientType clientType,
                                         final String zoneId) {
    return KpiData.builder()
        .createdIso(entity.getCreated())
        .created(
                DateTimeUtils.format(
                        DateTimeUtils.serverToClient(
                                entity.getCreated().plus(3, ChronoUnit.HOURS), clientType, zoneId),
                        "Z",
                        "Default",
                        "Default"))
        .value(entity.getValue())
        .build();
    }

    public SpeedTest speedTestEntityToObject(final DiagSpeedTestEntity entity, final ClientType clientType,
                                             final String zoneId) {
    return SpeedTest.builder()
        .value(entity.getValue())
        .createdIso(entity.getCreated())
        .created(
            DateTimeUtils.format(
                DateTimeUtils.serverToClient(
                    entity.getCreated().plus(3, ChronoUnit.HOURS), clientType, zoneId),
                "Z",
                "Default",
                "Default"))
        .build();
    }
}
