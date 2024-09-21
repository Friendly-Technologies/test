package com.friendly.services.productclass.orm.acs.model.projections;

public interface ProductGroupEventReportProjection {
    public String getDomainName();
    public String getActivityType();
    public Integer getCount();
}