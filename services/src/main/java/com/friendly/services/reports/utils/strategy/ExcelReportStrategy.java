package com.friendly.services.reports.utils.strategy;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.reports.utils.strategy.excel.ExcelReportGeneratorStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.EXCEL_STRATEGY_NOT_FOUND;

@Component
public class ExcelReportStrategy implements ReportStrategy {

    private final Map<ReportType, ExcelReportGeneratorStrategy> reportStrategyMap;

    public ExcelReportStrategy(List<ExcelReportGeneratorStrategy> strategies) {
        this.reportStrategyMap = strategies.stream()
                .collect(Collectors.toMap(ExcelReportGeneratorStrategy::getReportType, Function.identity()));
    }

    @Override
    public void generateReport(ReportType reportType, Session session, Map<String, Object> params, String fileName) {
        Optional.ofNullable(reportStrategyMap.get(reportType))
                .orElseThrow(() -> new FriendlyIllegalArgumentException(EXCEL_STRATEGY_NOT_FOUND, reportType))
                .generateReport(session, params, fileName);
    }

}
