package com.friendly.services.reports.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportStatus {
    IN_PROGRESS("In_progress"),
    ERROR("Error"),
    COMPLETED("Completed"),
    NOT_FOUND("Not found");

    private final String name;

    ReportStatus(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}

