package com.friendly.commons.models.device;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ProtocolType {
    TR069(0, "TR-069"),
    OMA(1, "OMA"),
    LWM2M(2, "LWM2M"),
    MQTT(3, "MQTT"),
    UNKNOWN(4, "Unknown"),
    USP(5, "USP"), // SCEF
    NOT_SET(100, "Not Set");

    private final int value;
    @JsonValue
    private final String name;
    private static final Map<Integer, ProtocolType> VALUE_MAP = new HashMap<>();

    static {
        for (ProtocolType protocolType : values()) {
            VALUE_MAP.put(protocolType.getValue(), protocolType);
        }
    }

    ProtocolType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ProtocolType fromValue(int value) {
        return VALUE_MAP.getOrDefault(value, NOT_SET);
    }
}
