package com.friendly.services.device.info.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ColumnId {
    DOMAIN_ID("domainId"),
    CREATED("created"),
    STATUS("status"),
    SERIAL("serial"),
    UPDATED("updated"),
    FIRMWARE("firmware"),
    PROTOCOL_TYPE("protocolType"),
    DOMAIN_NAME("domainName"),
    MANUFACTURER("manufacturer"),
    MODEL("model"),
    OUI("oui"),
    USER_LOGIN("userLogin"),
    USER_NAME("userName"),
    PHONE("phone"),
    ZIP("zip"),
    USER_LOCATION("userLocation"),
    USER_TAG("userTag"),
    USER_STATUS("userStatus"),
    USER_ID("userId"),
    CUST_1("cust1"),
    CUST_2("cust2"),
    CUST_3("cust3"),
    CUST_4("cust4"),
    CUST_5("cust5"),
    CUST_6("cust6"),
    CUST_7("cust7"),
    CUST_8("cust8"),
    CUST_9("cust9"),
    CUST_10("cust10"),
    COMPLETED_TASKS("completedTasks"),
    FAILED_TASKS("failedTasks"),
    PENDING_TASKS("pendingTasks"),
    REJECTED_TASKS("rejectedTasks"),
    SENT_TASKS("sentTasks"),
    HARDWARE("hardware"),
    SOFTWARE("software"),
    IP_ADDRESS("ipAddress"),
    MAC_ADDRESS("macAddress"),
    UPTIME("uptime"),
    LATITUDE("latitude"),
    LONGITUDE("longitude"),
    NODE_BID("nodeBID"),
    BSID("BSID"),
    ACS_USERNAME("acsUsername");

    private final String id;

    ColumnId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private static final Map<String, ColumnId> COLUMN_ID_MAP = new HashMap<>();

    static {
        for (ColumnId columnId : ColumnId.values()) {
            COLUMN_ID_MAP.put(columnId.getId(), columnId);
        }
    }

    public static Map<String, ColumnId> getColumnIdMap() {
        return Collections.unmodifiableMap(COLUMN_ID_MAP);
    }
}

