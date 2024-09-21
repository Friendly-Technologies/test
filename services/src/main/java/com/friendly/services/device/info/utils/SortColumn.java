package com.friendly.services.device.info.utils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public enum SortColumn {
    CREATED(ColumnId.CREATED, "Created"),
    STATUS(ColumnId.STATUS, "isOnline"),
    SERIAL(ColumnId.SERIAL, "Serial"),
    UPDATED(ColumnId.UPDATED, "updated"),
    FIRMWARE(ColumnId.FIRMWARE, "Firmware"),
    PROTOCOL_TYPE(ColumnId.PROTOCOL_TYPE, "protocolId"),
    DOMAIN_NAME(ColumnId.DOMAIN_NAME, "Domain"),
    DOMAIN_ID(ColumnId.DOMAIN_ID, "Domain"),
    MANUFACTURER(ColumnId.MANUFACTURER, "productClass.productGroup.manufacturerName"),
    MODEL(ColumnId.MODEL, "productClass.productGroup.model"),
    OUI(ColumnId.OUI, "productClass.manufacturer.oui"),
    USER_LOGIN(ColumnId.USER_LOGIN, "customDevice.userLogin"),
    USER_NAME(ColumnId.USER_NAME, "customDevice.userName"),
    PHONE(ColumnId.PHONE, "customDevice.phone"),
    ZIP(ColumnId.ZIP, "customDevice.zip"),
    USER_LOCATION(ColumnId.USER_LOCATION, "customDevice.userLocation"),
    USER_TAG(ColumnId.USER_TAG, "customDevice.userTag"),
    USER_STATUS(ColumnId.USER_STATUS, "customDevice.userStatus"),
    USER_ID(ColumnId.USER_ID, "customDevice.userId"),
    CUST_1(ColumnId.CUST_1, "customDevice.cust1"),
    CUST_2(ColumnId.CUST_2, "customDevice.cust2"),
    CUST_3(ColumnId.CUST_3, "customDevice.cust3"),
    CUST_4(ColumnId.CUST_4, "customDevice.cust4"),
    CUST_5(ColumnId.CUST_5, "customDevice.cust5"),
    CUST_6(ColumnId.CUST_6, "customDevice.cust6"),
    CUST_7(ColumnId.CUST_7, "customDevice.cust7"),
    CUST_8(ColumnId.CUST_8, "customDevice.cust8"),
    CUST_9(ColumnId.CUST_9, "customDevice.cust9"),
    CUST_10(ColumnId.CUST_10, "customDevice.cust10"),
    LATITUDE(ColumnId.LATITUDE, "customDevice.latitude"),
    LONGITUDE(ColumnId.LONGITUDE, "customDevice.longitude");

    private final ColumnId columnId;
    private final String sortValue;

    SortColumn(ColumnId columnId, String sortValue) {
        this.columnId = columnId;
        this.sortValue = sortValue;
    }

    public String getColumnIdValue() {
        return columnId.getId();
    }

    public ColumnId getColumnId() {
        return columnId;
    }

    public String getSortValue() {
        return sortValue;
    }

    private static final Map<ColumnId, String> SORT_MAP = new EnumMap<>(ColumnId.class);

    static {
        for (SortColumn sortColumn : SortColumn.values()) {
            SORT_MAP.put(sortColumn.getColumnId(), sortColumn.getSortValue());
        }
    }

    public static Map<ColumnId, String> getSortMap() {
        return Collections.unmodifiableMap(SORT_MAP);
    }

    public static String getSortValue(final String field) {
        ColumnId columnId = ColumnId.getColumnIdMap().get(field);
        return SORT_MAP.getOrDefault(columnId, field);
    }
}

