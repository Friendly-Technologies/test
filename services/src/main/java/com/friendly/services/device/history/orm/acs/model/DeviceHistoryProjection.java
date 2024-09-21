package com.friendly.services.device.history.orm.acs.model;

import java.time.LocalDate;

public interface DeviceHistoryProjection {
    LocalDate getCreated();
    Integer getEventCodeId();
    Long getCount();
}
