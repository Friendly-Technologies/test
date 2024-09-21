package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileConditionNameEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileAutomationParametersRequest {
    private Integer id;
    private DeviceProfileConditionNameEnum conditionName;
    private String fullName;
    private String conditionValue;
    private Integer status;
    private List<AbstractActionRequest> actionsRequests;
}
