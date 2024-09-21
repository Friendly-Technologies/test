package com.friendly.services.device.info.utils;

import com.friendly.commons.models.view.FilterType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public enum ColumnFilter {
    CREATED(ColumnId.CREATED, FilterType.Date),
    STATUS(ColumnId.STATUS, FilterType.OnOrOff),
    SERIAL(ColumnId.SERIAL, FilterType.Serial),
    UPDATED(ColumnId.UPDATED, FilterType.Date),
    FIRMWARE(ColumnId.FIRMWARE, FilterType.TextValue),
    PROTOCOL_TYPE(ColumnId.PROTOCOL_TYPE, FilterType.Protocol),
    MANUFACTURER(ColumnId.MANUFACTURER, FilterType.Manufacturer),
    MODEL(ColumnId.MODEL, FilterType.Model),
    OUI(ColumnId.OUI, FilterType.TextValue),
    MAC_ADDRESS(ColumnId.MAC_ADDRESS, FilterType.MacAddress),
    USER_LOGIN(ColumnId.USER_LOGIN, FilterType.TextValue),
    USER_NAME(ColumnId.USER_NAME, FilterType.TextValue),
    PHONE(ColumnId.PHONE, FilterType.TextValue),
    ZIP(ColumnId.ZIP, FilterType.TextValue),
    USER_LOCATION(ColumnId.USER_LOCATION, FilterType.TextValue),
    USER_TAG(ColumnId.USER_TAG, FilterType.TextValue),
    USER_STATUS(ColumnId.USER_STATUS, FilterType.TextValue),
    USER_ID(ColumnId.USER_ID, FilterType.TextValue),
    ACS_USERNAME(ColumnId.ACS_USERNAME, FilterType.TextValue),
    BSID(ColumnId.BSID, FilterType.TextValue),
    NODE_BID(ColumnId.NODE_BID, FilterType.TextValue),
    CUST_1(ColumnId.CUST_1, FilterType.TextValue),
    CUST_2(ColumnId.CUST_2, FilterType.TextValue),
    CUST_3(ColumnId.CUST_3, FilterType.TextValue),
    CUST_4(ColumnId.CUST_4, FilterType.TextValue),
    CUST_5(ColumnId.CUST_5, FilterType.TextValue),
    CUST_6(ColumnId.CUST_6, FilterType.TextValue),
    CUST_7(ColumnId.CUST_7, FilterType.TextValue),
    CUST_8(ColumnId.CUST_8, FilterType.TextValue),
    CUST_9(ColumnId.CUST_9, FilterType.TextValue),
    CUST_10(ColumnId.CUST_10, FilterType.TextValue),
    LATITUDE(ColumnId.LATITUDE, FilterType.Coordinate),
    LONGITUDE(ColumnId.LONGITUDE, FilterType.Coordinate),
    HARDWARE(ColumnId.HARDWARE, FilterType.TextValue),
    SOFTWARE(ColumnId.SOFTWARE, FilterType.TextValue),
    IP_ADDRESS(ColumnId.IP_ADDRESS, FilterType.IpAddress),
    UPTIME(ColumnId.UPTIME, FilterType.TextValue),
    DOMAIN_ID(ColumnId.DOMAIN_ID, FilterType.Domain);

    private final ColumnId columnId;
    private final FilterType filterType;

    ColumnFilter(ColumnId columnId, FilterType filterType) {
        this.columnId = columnId;
        this.filterType = filterType;
    }

    public String getColumnIdValue() {
        return columnId.getId();
    }

    public ColumnId getColumnId() {
        return columnId;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    private static final Map<ColumnId, FilterType> COLUMN_FILTER_MAP = new EnumMap<>(ColumnId.class);

    static {
        for (ColumnFilter columnFilter : values()) {
            COLUMN_FILTER_MAP.put(columnFilter.getColumnId(), columnFilter.getFilterType());
        }
    }

    public static Map<ColumnId, FilterType> getColumnFilterMap() {
        return Collections.unmodifiableMap(COLUMN_FILTER_MAP);
    }
}
