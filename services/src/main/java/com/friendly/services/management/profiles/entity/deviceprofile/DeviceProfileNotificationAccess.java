package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileAccessEnum;
import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileNotificationEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileNotificationAccess {
    private String fullName;
    private DeviceProfileNotificationEnum notification;
    private DeviceProfileAccessEnum accessList;
}
