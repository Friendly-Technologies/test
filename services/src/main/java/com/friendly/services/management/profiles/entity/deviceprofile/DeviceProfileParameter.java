package com.friendly.services.management.profiles.entity.deviceprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileParameter {
    private String fullName;
    private String value;
}
