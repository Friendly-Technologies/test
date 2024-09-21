package com.friendly.services.device.info.orm.acs.model.projections;

import java.time.Instant;

public interface CpeRegReportProjection extends CpeBaseInfoProjection {
    public Instant getCreated();
    public Instant getUpdated();
    public String getPhone();
}