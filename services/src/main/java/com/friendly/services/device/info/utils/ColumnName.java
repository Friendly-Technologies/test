package com.friendly.services.device.info.utils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public enum ColumnName {
    DOMAIN_ID(ColumnId.DOMAIN_ID, "Domain"),
    CREATED(ColumnId.CREATED, "Created"),
    STATUS(ColumnId.STATUS, "Status"),
    SERIAL(ColumnId.SERIAL, "Serial"),
    UPDATED(ColumnId.UPDATED, "Last connection"),
    FIRMWARE(ColumnId.FIRMWARE, "Firmware"),
    PROTOCOL_TYPE(ColumnId.PROTOCOL_TYPE, "Protocol"),
    DOMAIN_NAME(ColumnId.DOMAIN_NAME, "Domain"),
    MANUFACTURER(ColumnId.MANUFACTURER, "Manufacturer"),
    MODEL(ColumnId.MODEL, "Model"),
    OUI(ColumnId.OUI, "OUI"),
    USER_LOGIN(ColumnId.USER_LOGIN, "User login"),
    USER_NAME(ColumnId.USER_NAME, "User name"),
    PHONE(ColumnId.PHONE, "Phone"),
    ZIP(ColumnId.ZIP, "Zip"),
    USER_LOCATION(ColumnId.USER_LOCATION, "User location"),
    USER_TAG(ColumnId.USER_TAG, "User tag"),
    USER_STATUS(ColumnId.USER_STATUS, "User status"),
    USER_ID(ColumnId.USER_ID, "User ID"),
    CUST_1(ColumnId.CUST_1, "cust1"),
    CUST_2(ColumnId.CUST_2, "cust2"),
    CUST_3(ColumnId.CUST_3, "cust3"),
    CUST_4(ColumnId.CUST_4, "cust4"),
    CUST_5(ColumnId.CUST_5, "cust5"),
    CUST_6(ColumnId.CUST_6, "cust6"),
    CUST_7(ColumnId.CUST_7, "cust7"),
    CUST_8(ColumnId.CUST_8, "cust8"),
    CUST_9(ColumnId.CUST_9, "cust9"),
    CUST_10(ColumnId.CUST_10, "cust10"),
    COMPLETED_TASKS(ColumnId.COMPLETED_TASKS, "Completed tasks"),
    FAILED_TASKS(ColumnId.FAILED_TASKS, "Failed tasks"),
    PENDING_TASKS(ColumnId.PENDING_TASKS, "Pending tasks"),
    REJECTED_TASKS(ColumnId.REJECTED_TASKS, "Rejected tasks"),
    SENT_TASKS(ColumnId.SENT_TASKS, "Sent tasks"),
    HARDWARE(ColumnId.HARDWARE, "Hardware"),
    SOFTWARE(ColumnId.SOFTWARE, "Software"),
    IP_ADDRESS(ColumnId.IP_ADDRESS, "IP address"),
    MAC_ADDRESS(ColumnId.MAC_ADDRESS, "MAC address"),
    UPTIME(ColumnId.UPTIME, "Uptime"),
    LATITUDE(ColumnId.LATITUDE, "Latitude"),
    LONGITUDE(ColumnId.LONGITUDE, "Longitude"),
    NODE_BID(ColumnId.NODE_BID, "E-UTRAN Node B ID"),
    BSID(ColumnId.BSID, "Base Station ID"),
    ACS_USERNAME(ColumnId.ACS_USERNAME, "ACS Username");

    private final ColumnId columnId;
    private final String name;

    ColumnName(ColumnId columnId, String name) {
        this.columnId = columnId;
        this.name = name;
    }

    public String getColumnIdValue() {
        return columnId.getId();
    }

    public ColumnId getColumnId() {
        return columnId;
    }

    public String getName() {
        return name;
    }

    private static final Map<ColumnId, String> COLUMN_NAME_MAP = new EnumMap<>(ColumnId.class);

    static {
        for (ColumnName columnName : ColumnName.values()) {
            COLUMN_NAME_MAP.put(columnName.getColumnId(), columnName.getName());
        }
    }

    public static Map<ColumnId, String> getColumnNameMap() {
        return Collections.unmodifiableMap(COLUMN_NAME_MAP);
}

    public static boolean isColumnInNameMap(String columnKey) {
        ColumnId id = ColumnId.getColumnIdMap().get(columnKey);
        return COLUMN_NAME_MAP.containsKey(id);
    }
}
