package com.friendly.services.productclass.orm.acs.model.projections;

import java.util.Date;

public interface ProductGroupOnlineReportProjection extends ProductGroupWithCountProjection {
    public Date getDate();
}