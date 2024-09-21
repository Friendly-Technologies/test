package com.friendly.services.qoemonitoring.orm.qoe.model.projections;

import java.time.Instant;

public interface UserExpHostProjection {
    Instant getCreated();
    String getSerial();
    Integer getNameId();
    String getMac();
    String getInterfaceType();
    String getLayer1();
    String getLayer3();
    String getActive();
    String getHostName();
}
