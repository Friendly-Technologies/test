package com.friendly.services.qoemonitoring.orm.qoe.model.projections;

import java.time.Instant;

public interface CpeDataProjection {
    String getSerial();
    Integer getNameId();
    Instant getCreated();
    String getValue();
}
