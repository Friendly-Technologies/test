package com.friendly.services.qoemonitoring.orm.qoe.model.projections;

import java.time.Instant;

public interface UserExpAssocDeviceProjection {
    String getSerial();
    Integer getNameId();
    Instant getCreated();
    String getMac();
    String getRssi();
    String getSignal();
}
