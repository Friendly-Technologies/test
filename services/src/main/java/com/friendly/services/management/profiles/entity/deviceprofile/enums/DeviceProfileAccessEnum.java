package com.friendly.services.management.profiles.entity.deviceprofile.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DeviceProfileAccessEnum {
    DEFAULT(null,
            "Default"),
    ACS_ONLY("",
            "AcsOnly"),
    ALL("Subscribed",
            "All");

    @Getter
    private final String value;
    private final String description;
    @JsonValue
    public String getDescription() {
        return description;
    }
}
