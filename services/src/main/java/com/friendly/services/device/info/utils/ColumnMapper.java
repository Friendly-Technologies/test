package com.friendly.services.device.info.utils;

import com.friendly.commons.models.device.Device;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public enum ColumnMapper {
    DOMAIN_NAME(ColumnId.DOMAIN_NAME, Device::getDomainName),
    CREATED(ColumnId.CREATED, Device::getCreated),
    STATUS(ColumnId.STATUS, Device::getStatus),
    SERIAL(ColumnId.SERIAL, Device::getSerial),
    UPDATED(ColumnId.UPDATED, Device::getUpdated),
    FIRMWARE(ColumnId.FIRMWARE, Device::getFirmware),
    PROTOCOL_TYPE(ColumnId.PROTOCOL_TYPE, Device::getProtocolType),
    MANUFACTURER(ColumnId.MANUFACTURER, Device::getManufacturer),
    MODEL(ColumnId.MODEL, Device::getModel),
    OUI(ColumnId.OUI, Device::getOui),
    USER_LOGIN(ColumnId.USER_LOGIN, Device::getUserLogin),
    USER_NAME(ColumnId.USER_NAME, Device::getUserName),
    PHONE(ColumnId.PHONE, Device::getPhone),
    ZIP(ColumnId.ZIP, Device::getZip),
    USER_LOCATION(ColumnId.USER_LOCATION, Device::getUserLocation),
    USER_TAG(ColumnId.USER_TAG, Device::getUserTag),
    USER_STATUS(ColumnId.USER_STATUS, Device::getUserStatus),
    USER_ID(ColumnId.USER_ID, Device::getUserId),
    CUST_1(ColumnId.CUST_1, Device::getCust1),
    CUST_2(ColumnId.CUST_2, Device::getCust2),
    CUST_3(ColumnId.CUST_3, Device::getCust3),
    CUST_4(ColumnId.CUST_4, Device::getCust4),
    CUST_5(ColumnId.CUST_5, Device::getCust5),
    CUST_6(ColumnId.CUST_6, Device::getCust6),
    CUST_7(ColumnId.CUST_7, Device::getCust7),
    CUST_8(ColumnId.CUST_8, Device::getCust8),
    CUST_9(ColumnId.CUST_9, Device::getCust9),
    CUST_10(ColumnId.CUST_10, Device::getCust10),
    COMPLETED_TASKS(ColumnId.COMPLETED_TASKS, Device::getCompletedTasks),
    FAILED_TASKS(ColumnId.FAILED_TASKS, Device::getFailedTasks),
    PENDING_TASKS(ColumnId.PENDING_TASKS, Device::getPendingTasks),
    REJECTED_TASKS(ColumnId.REJECTED_TASKS, Device::getRejectedTasks),
    SENT_TASKS(ColumnId.SENT_TASKS, Device::getSentTasks),
    HARDWARE(ColumnId.HARDWARE, Device::getHardware),
    SOFTWARE(ColumnId.SOFTWARE, Device::getSoftware),
    IP_ADDRESS(ColumnId.IP_ADDRESS, Device::getIpAddress),
    MAC_ADDRESS(ColumnId.MAC_ADDRESS, Device::getMacAddress),
    UPTIME(ColumnId.UPTIME, Device::getUptime),
    LATITUDE(ColumnId.LATITUDE, device -> ""),
    LONGITUDE(ColumnId.LONGITUDE, device -> "");

    public static final String UNKNOWN = "Unknown";
    private final ColumnId columnId;
    private final Function<Device, Object> mapper;


    ColumnMapper(ColumnId columnId, Function<Device, Object> mapper) {
        this.columnId = columnId;
        this.mapper = mapper;
    }

    public String getColumnIdValue() {
        return columnId.getId();
    }

    public ColumnId getColumnId() {
        return columnId;
    }

    public Function<Device, Object> getMapper() {
        return mapper;
    }

    private static final Map<ColumnId, Function<Device, Object>> COLUMN_MAPPER_MAP = new EnumMap<>(ColumnId.class);

    static {
        for (ColumnMapper columnMapper : values()) {
            COLUMN_MAPPER_MAP.put(columnMapper.getColumnId(), columnMapper.getMapper());
        }
    }

    public static Map<ColumnId, Function<Device, Object>> getColumnMapperMap() {
        return Collections.unmodifiableMap(COLUMN_MAPPER_MAP);
    }

    public static Object matchDeviceParamToColumnName(final Device device, final String columnKey) {
        try {
            ColumnId id = ColumnId.getColumnIdMap().get(columnKey);
            return COLUMN_MAPPER_MAP.getOrDefault(id, d -> UNKNOWN).apply(device);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

}
