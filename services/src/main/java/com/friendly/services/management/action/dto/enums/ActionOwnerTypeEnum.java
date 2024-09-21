package com.friendly.services.management.action.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActionOwnerTypeEnum {
    UPDATE_GROUP(1),
    PROFILE_AUTOMATION_PARAMETERS(2),
    PROFILE_AUTOMATION_EVENTS(3),
    EVENT_PARAMETER_MONITOR(4),
    EVENT(5);

    private final Integer ownerType;
}