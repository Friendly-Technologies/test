package com.friendly.services.management.profiles;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ConditionGroupType {
    Inform("Inform"),
    UserInfo("User info");

    private final String value;

    ConditionGroupType(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    public static ConditionGroupType fromValue(String v) {
        for (ConditionGroupType state : values()) {
            if (state.value.equals(v)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ConditionGroupType value: " + v);
    }

    public static List<String> getValues() {
        return Arrays.stream(values()).map(v -> v.value).collect(Collectors.toList());
    }
}