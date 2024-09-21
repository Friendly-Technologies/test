package com.friendly.services.management.groupupdate.orm.acs.model.projection;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateDeviceStateType;

public interface DevicesStatusProjection {
    GroupUpdateDeviceStateType getState();
    long getCount();
}