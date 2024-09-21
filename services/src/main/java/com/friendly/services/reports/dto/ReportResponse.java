package com.friendly.services.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportResponse {
    private String fileName;
    private ReportStatus status;
}

