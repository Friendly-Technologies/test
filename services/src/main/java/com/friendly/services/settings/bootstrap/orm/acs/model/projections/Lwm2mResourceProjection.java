package com.friendly.services.settings.bootstrap.orm.acs.model.projections;

public interface Lwm2mResourceProjection {
    Integer getId();
    Integer getObjectId();
    String getName();
    String getDescription();
    Integer getInstanceType();
    String getVersion();
}