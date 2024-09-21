package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileAutomationEventsRequest {
    private Integer id;
    private Integer duration;
    private String eventName;
    private Integer countOfEvents;
    private Boolean onEachEvent;
    private Integer status;
    private List<AbstractActionRequest> actionsRequests;
}
