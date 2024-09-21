package com.friendly.services.management.profiles.entity.deviceprofile.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Getter
public enum DeviceProfileConditionNameEnum {
    GREATER(1),
    EQUAL(2),
    LESS_EQUAL(3),
    LESS(4),
    CONTAINS(5),
    NOT_EQUAL(6),
    STARTS_WITH(7),
    GREATER_EQUAL(8),
    VALUE_CHANGE(9),
    REGEXP(10);

    private final Integer id;

    private static final Map<Integer, DeviceProfileConditionNameEnum> ID_TO_ENUM_MAP = new HashMap<>();

    static {
        for (DeviceProfileConditionNameEnum e : values()) {
            ID_TO_ENUM_MAP.put(e.id, e);
        }
    }

    DeviceProfileConditionNameEnum(Integer id) {
        this.id = id;
    }

    public static DeviceProfileConditionNameEnum getEnumById(Integer id) {
        DeviceProfileConditionNameEnum result = ID_TO_ENUM_MAP.get(id);
        if (result == null) {
            throw new IllegalArgumentException("No enum with " + id + " id found");
        }
        return result;
    }
}
