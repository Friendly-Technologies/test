package com.friendly.services.settings.bootstrap.orm.acs.model.projections;

public interface Lwm2mResourceInstancesProjection {
    Integer getInstanceId();
    Integer getResourceInstanceId();
    String getValue();
    String getName();
}