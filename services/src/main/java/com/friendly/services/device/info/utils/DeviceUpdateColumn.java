package com.friendly.services.device.info.utils;

import com.friendly.commons.models.device.DeviceColumns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum DeviceUpdateColumn {
    DOMAIN_ID(ColumnId.DOMAIN_ID, false, true, false),
    CREATED(ColumnId.CREATED, true, true, true),
    STATUS(ColumnId.STATUS, true, true, true),
    SERIAL(ColumnId.SERIAL, true, true, true),
    UPDATED(ColumnId.UPDATED, true, true, true),
    FIRMWARE(ColumnId.FIRMWARE, true, true, true),
    PROTOCOL_TYPE(ColumnId.PROTOCOL_TYPE, true, true, true),
    DOMAIN_NAME(ColumnId.DOMAIN_NAME, true, false, true),
    MANUFACTURER(ColumnId.MANUFACTURER, true, true, true),
    MODEL(ColumnId.MODEL, true, true, true),
    OUI(ColumnId.OUI, true, true, true),
    USER_LOGIN(ColumnId.USER_LOGIN, true, true, true),
    USER_NAME(ColumnId.USER_NAME, true, true, true),
    PHONE(ColumnId.PHONE, true, true, true),
    ZIP(ColumnId.ZIP, true, true, true),
    USER_LOCATION(ColumnId.USER_LOCATION, true, true, true),
    USER_TAG(ColumnId.USER_TAG, true, true, true),
    USER_STATUS(ColumnId.USER_STATUS, true, true, true),
    USER_ID(ColumnId.USER_ID, true, true, true),
    CUST_1(ColumnId.CUST_1, true, true, true),
    CUST_2(ColumnId.CUST_2, true, true, true),
    CUST_3(ColumnId.CUST_3, true, true, true),
    CUST_4(ColumnId.CUST_4, true, true, true),
    CUST_5(ColumnId.CUST_5, true, true, true),
    CUST_6(ColumnId.CUST_6, true, true, true),
    CUST_7(ColumnId.CUST_7, true, true, true),
    CUST_8(ColumnId.CUST_8, true, true, true),
    CUST_9(ColumnId.CUST_9, true, true, true),
    CUST_10(ColumnId.CUST_10, true, true, true),
    LATITUDE(ColumnId.LATITUDE, true, true, true),
    LONGITUDE(ColumnId.LONGITUDE, true, true, true),
    HARDWARE(ColumnId.HARDWARE, false, true, true),
    SOFTWARE(ColumnId.SOFTWARE, false, true, true),
    IP_ADDRESS(ColumnId.IP_ADDRESS, false, true, true),
    MAC_ADDRESS(ColumnId.MAC_ADDRESS, false, true, true),
    UPTIME(ColumnId.UPTIME, false, true, true),
    NODE_BID(ColumnId.NODE_BID, false, true, true),
    BSID(ColumnId.BSID, false, true, true),
    COMPLETED_TASKS(ColumnId.COMPLETED_TASKS, false, false, true),
    FAILED_TASKS(ColumnId.FAILED_TASKS, false, false, true),
    PENDING_TASKS(ColumnId.PENDING_TASKS, false, false, true),
    REJECTED_TASKS(ColumnId.REJECTED_TASKS, false, false, true),
    SENT_TASKS(ColumnId.SENT_TASKS, false, false, true),
    ACS_USERNAME(ColumnId.ACS_USERNAME, false, true, true);

    private final ColumnId columnId;
    private final boolean canSort;
    private final boolean canUpdate;
    private final boolean canCreate;

    DeviceUpdateColumn(ColumnId columnId, boolean canSort, boolean canUpdate, boolean canCreate) {
        this.columnId = columnId;
        this.canSort = canSort;
        this.canUpdate = canUpdate;
        this.canCreate = canCreate;
    }

    public String getColumnIdValue() {
        return columnId.getId();
    }

    public ColumnId getColumnId() {
        return columnId;
    }

    public boolean isCanSort() {
        return canSort;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public boolean isCanCreate() {
        return canCreate;
    }

    private static final List<DeviceColumns> DEVICE_UPDATE_COLUMNS_LIST = new ArrayList<>();

    static {
        for (DeviceUpdateColumn column : DeviceUpdateColumn.values()) {
            DEVICE_UPDATE_COLUMNS_LIST.add(new DeviceColumns(column.getColumnId().getId(), column.isCanSort(),
                    column.isCanUpdate(), column.isCanCreate()));
        }
    }

    public static List<DeviceColumns> getDeviceUpdateColumnsList() {
        return Collections.unmodifiableList(DEVICE_UPDATE_COLUMNS_LIST);
    }

    public static Boolean canSort(final String columnKey) {
        return DEVICE_UPDATE_COLUMNS_LIST.stream()
                .filter(d -> d.getColumnKey().equals(columnKey))
                .map(DeviceColumns::isCanSort)
                .findAny()
                .orElse(null);
    }
}

