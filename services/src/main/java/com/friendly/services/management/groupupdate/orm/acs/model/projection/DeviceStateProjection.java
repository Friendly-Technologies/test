package com.friendly.services.management.groupupdate.orm.acs.model.projection;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateDeviceStateType;

public interface DeviceStateProjection {
    Long getId();
    String getSerial();
    Integer getSelected();
    GroupUpdateDeviceStateType getState();
}