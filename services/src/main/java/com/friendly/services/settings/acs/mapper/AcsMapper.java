package com.friendly.services.settings.acs.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.acs.whitelist.AcsWhiteListIp;
import com.friendly.commons.models.settings.acs.whitelist.AcsWhiteListSerial;
import com.friendly.commons.models.settings.acs.whitelist.WhiteListType;
import com.friendly.services.settings.acs.orm.acs.model.AcsLicenseParameterEntity;
import com.friendly.services.settings.acs.orm.acs.model.WhiteListEntity;
import com.friendly.services.settings.acs.model.License;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.LicenseUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
public class AcsMapper {

    private static final Map<String, BiConsumer<License.LicenseBuilder, String>> ENTITY_MAPPING = new HashMap<>();

    static {
        ENTITY_MAPPING.put("CPEAdminUsers", License.LicenseBuilder::cpeAdminUsers);
        ENTITY_MAPPING.put("CSRUsers", License.LicenseBuilder::csrUsers);
        ENTITY_MAPPING.put("CustomerName", License.LicenseBuilder::customerName);
        ENTITY_MAPPING.put("TimeExpiration", License.LicenseBuilder::timeExpiration);
        ENTITY_MAPPING.put("RegisteredCPE", License.LicenseBuilder::registeredCpe);
        ENTITY_MAPPING.put("RegisteredCPETR069", License.LicenseBuilder::registeredCpeTR069);
        ENTITY_MAPPING.put("RegisteredCPEUSP", License.LicenseBuilder::registeredCpeUSP);
        ENTITY_MAPPING.put("RegisteredCPELWM2M", License.LicenseBuilder::registeredCpeLWM2M);
        ENTITY_MAPPING.put("RegisteredCPEMQTT", License.LicenseBuilder::registeredCpeMQTT);
        ENTITY_MAPPING.put("ManagedCPE", License.LicenseBuilder::managedCpe);
        ENTITY_MAPPING.put("ManagedCPETR069", License.LicenseBuilder::managedCpeTR069);
        ENTITY_MAPPING.put("ManagedCPEUSP", License.LicenseBuilder::managedCpeUSP);
        ENTITY_MAPPING.put("ManagedCPELWM2M", License.LicenseBuilder::managedCpeLWM2M);
        ENTITY_MAPPING.put("ManagedCPEMQTT", License.LicenseBuilder::managedCpeMQTT);
        ENTITY_MAPPING.put("DayCount", License.LicenseBuilder::dayCount);
    }

    public License entitiesToLicense(final List<AcsLicenseParameterEntity> entities) {
        final License.LicenseBuilder builder = License.builder();
        entities.forEach(entity -> {
            BiConsumer<License.LicenseBuilder, String> consumer = ENTITY_MAPPING.get(entity.getName());
            if (consumer != null) {
                consumer.accept(builder, LicenseUtils.decryptLicenseWithCheck(entity.getValue()));
            }
        });
        return builder.build();
    }

    public List<AcsWhiteListIp> whiteListEntitiesToWhiteListIps(final List<WhiteListEntity> entities,
                                                                final ClientType clientType,
                                                                final String zoneId, final String dateFormat,
                                                                final String timeFormat) {
        return entities.stream()
                .map(e -> whiteListEntityToWhiteListIp(e, clientType, zoneId, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    private AcsWhiteListIp whiteListEntityToWhiteListIp(final WhiteListEntity entity, final ClientType clientType,
                                                        final String zoneId, final String dateFormat,
                                                        final String timeFormat) {
        return AcsWhiteListIp.builder()
                .id(entity.getId())
                .type(WhiteListType.IP_RANGE)
                .creator(entity.getCreator())
                .createdIso(DateTimeUtils.convertTimeToIso(entity.getCreated(), zoneId))
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .onlyCreated(entity.getOnlyCreated())
                .ipRange(entity.getIpRange())
                .manufacturer(entity.getManufacturer())
                .model(entity.getModel())
                .build();
    }

    public List<AcsWhiteListSerial> whiteListEntitiesToWhiteListSerials(final List<WhiteListEntity> entities,
                                                                        final ClientType clientType,
                                                                        final String zoneId, final String dateFormat,
                                                                        final String timeFormat) {
        return entities.stream()
                .map(e -> whiteListEntityToWhiteListSerial(e, clientType, zoneId, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    private AcsWhiteListSerial whiteListEntityToWhiteListSerial(final WhiteListEntity entity,
                                                                final ClientType clientType,
                                                                final String zoneId, final String dateFormat,
                                                                final String timeFormat) {
        return AcsWhiteListSerial.builder()
                .id(entity.getId())
                .type(WhiteListType.SERIAL)
                .creator(entity.getCreator())
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .createdIso(DateTimeUtils.convertTimeToIso(entity.getCreated(), zoneId))
                .onlyCreated(entity.getOnlyCreated())
                .description(entity.getDescription())
                .typeSerial(entity.getTypeSerial())
                .build();
    }

}
