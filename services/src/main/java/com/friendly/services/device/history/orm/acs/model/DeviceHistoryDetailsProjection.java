package com.friendly.services.device.history.orm.acs.model;

import java.time.Instant;

public interface DeviceHistoryDetailsProjection {
    String getPrevValue();
    String getCurValue();
    Instant getCreated();
    Long getNameId();
}