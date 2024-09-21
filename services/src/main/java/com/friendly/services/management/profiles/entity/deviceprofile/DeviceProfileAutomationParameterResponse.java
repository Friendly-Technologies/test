package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileConditionNameEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileAutomationParameterResponse {
    private Integer id;
    private DeviceProfileConditionNameEnum conditionName;
    private String fullName;
    private String conditionValue;
    private Boolean isAnyTaskHere;
}
