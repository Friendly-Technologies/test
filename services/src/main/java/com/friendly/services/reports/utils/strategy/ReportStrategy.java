package com.friendly.services.reports.utils.strategy;

import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;

import java.util.Map;

public interface ReportStrategy {
    void generateReport(ReportType reportType, Session session, Map<String, Object> params, String fileName);
}
