package com.friendly.services.management.profiles.orm.acs.model;

import com.friendly.services.productclass.orm.acs.model.projections.ProductGroupInfoProjection;

import java.time.Instant;

public interface ProfileDownloadReportProjection extends ProductGroupInfoProjection {
    Long getProfileId();
    String getProfileName();
    String getFileType();
    Instant getCreated();
    String getCreator();
    String getProfileVersion();
    Integer getPending();
    Integer getRejected();
    Integer getCompleted();
    Integer getFailed();
    String getUrl();
}