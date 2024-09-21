package com.friendly.services.management.profiles.entity.deviceprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileAutomationEventsResponse {
    private Integer id;
    private Integer duration;
    private String eventName;
    private Integer countOfEvents;
    private Boolean onEachEvent;
    private Boolean isAnyTaskHere;
}
