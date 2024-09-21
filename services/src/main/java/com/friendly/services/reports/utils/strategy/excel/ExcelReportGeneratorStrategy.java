package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;

import java.util.Map;

public interface ExcelReportGeneratorStrategy {
    void generateReport(final Session session, final Map<String, Object> params, String fileName);

    ReportType getReportType();
}
