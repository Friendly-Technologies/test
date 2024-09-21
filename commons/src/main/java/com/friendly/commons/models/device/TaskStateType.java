package com.friendly.commons.models.device;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStateType {
    COMPLETED("Completed"),
    PENDING("Pending"),
    SENT("Sent"),
    REJECTED("Rejected"),
    FAILED("Failed");

    private final String value;

    TaskStateType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static TaskStateType fromValue(String v) {
        for (TaskStateType state : values()) {
            if (state.value.equals(v)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid TaskStateType value: " + v);
    }
}
