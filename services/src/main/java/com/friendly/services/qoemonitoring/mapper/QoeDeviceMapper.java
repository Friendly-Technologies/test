package com.friendly.services.qoemonitoring.mapper;

import com.friendly.commons.cache.CpeParameterNameCache;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.CpeData;
import com.friendly.commons.models.device.DiagIpPing;
import com.friendly.commons.models.device.NetworkMap;
import com.friendly.commons.models.device.UserExperienceConditionFilter;
import com.friendly.commons.models.device.UserExperienceConditionType;
import com.friendly.commons.models.device.WifiEvent;
import com.friendly.commons.models.device.frame.ConditionFilter;
import com.friendly.commons.models.device.frame.ConditionType;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagIpPingEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.WifiCollisionEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.projections.UserExpAssocDeviceProjection;
import com.friendly.services.qoemonitoring.orm.qoe.model.projections.UserExpHostProjection;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class QoeDeviceMapper {
    private CpeParameterNameCache cpeParameterNameCache;
    public DiagIpPing userExperiencePingFromEntity(final DiagIpPingEntity entity, final ClientType clientType,
                                                   final String zoneId) {
        return DiagIpPing.builder()
                .createdIso(entity.getCreated())
                .created(
                        DateTimeUtils.format(
                                DateTimeUtils.serverToClient(
                                        entity.getCreated().plus(3, ChronoUnit.HOURS), clientType, zoneId),
                                "Z",
                                "Default",
                                "Default"))
                .value(entity.getValue())
                .serial(entity.getSerial())
                .build();
    }

    public WifiEvent userExperienceWifiEventFromEntity(final WifiCollisionEntity entity,
                                                       final ClientType clientType,
                                                       final String zoneId) {
        return WifiEvent.builder()
                .createdIso(entity.getCreated())
                .created(
                        DateTimeUtils.format(
                                DateTimeUtils.serverToClient(
                                        entity.getCreated().plus(3, ChronoUnit.HOURS), clientType, zoneId),
                                "Z",
                                "Default",
                                "Default"))
                .value(entity.getValue())
                .channel(entity.getChannel())
                .name(entity.getName())
                .frequency(entity.getNameId() == 24 ? "2.4 GHz" : "5 GHz")
                .build();
    }

    public ConditionFilter userExperienceConditionToConditionType(final UserExperienceConditionFilter conditions) {
        return ConditionFilter.builder()
                .compare(mapCompareValue(conditions.getCompare()))
                .conditionDateIso(conditions.getConditionDateIso())
                .conditionFromDateIso(conditions.getConditionFromDateIso())
                .conditionToDateIso(conditions.getConditionToDateIso())
                .conditionString(conditions.getConditionString())
                .build();
    }

    private static ConditionType mapCompareValue(final UserExperienceConditionType value) {
        for (ConditionType type : ConditionType.values()) {
            if (type.name().equals(value.name())) {
                return type;
            }
        }
        return null;
    }

    public NetworkMap cpeDataFromUserExp(UserExpHostProjection projection) {
        return NetworkMap.builder()
                .created(projection.getCreated())
                .hostname(projection.getHostName())
                .active(projection.getActive())
                .layer1Interface(projection.getLayer1())
                .layer3Interface(projection.getLayer3())
                .mac(projection.getMac())
                .interfaceType(projection.getInterfaceType())
                .name(cpeParameterNameCache.getNameById(projection.getNameId()))
                .build();
    }

    public CpeData cpeDataFromUserExpAssoc(UserExpAssocDeviceProjection projection) {
        return CpeData.builder()
                .serial(projection.getSerial())
                .nameId(projection.getNameId())
                .created(projection.getCreated())
                .mac(projection.getMac())
                .signal(projection.getSignal())
                .rssi(projection.getRssi())
                .name(cpeParameterNameCache.getNameById(projection.getNameId()))
                .build();
    }
}