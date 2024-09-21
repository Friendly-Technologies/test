package com.friendly.services.management.profiles.entity.deviceprofile.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum DeviceProfileNotificationEnum {
    DEFAULT(null,
            "Default"),
    OFF(0,
            "Off"),
    PASSIVE(1,
            "Passive"),
    ACTIVE(2,
            "Active");


    private final Integer value;
    private final String description;
    private static final Map<Integer, String> VALUE_TO_DESCRIPTION_ENUM_MAP = new HashMap<>();
    private static final Map<Integer, DeviceProfileNotificationEnum> VALUE_TO_ENUM_MAP = new HashMap<>();
    private static final Map<String, Integer> DESCRIPTION_TO_VALUE_ENUM_MAP = new HashMap<>();

    @JsonValue
    public String getDescription() {
        return description;
    }

    static {
        for (DeviceProfileNotificationEnum e : values()) {
            VALUE_TO_DESCRIPTION_ENUM_MAP.put(e.value, e.description);
        }
        for (DeviceProfileNotificationEnum e : values()) {
            VALUE_TO_ENUM_MAP.put(e.value, e);
        }
        for (DeviceProfileNotificationEnum e : values()) {
            DESCRIPTION_TO_VALUE_ENUM_MAP.put(e.description, e.value);
        }
    }


    public static DeviceProfileNotificationEnum getEnumByValue(Integer value) {
        DeviceProfileNotificationEnum result = VALUE_TO_ENUM_MAP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("No enum with " + value + " value found");
        }
        return result;
    }

    public static Integer getValueByDescription(String description) {
        return DESCRIPTION_TO_VALUE_ENUM_MAP.get(description);
    }

    public static String getDescriptionByValue(Integer value) {
        String result = VALUE_TO_DESCRIPTION_ENUM_MAP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("No description with " + value + " value found");
        }
        return result;
    }
}
