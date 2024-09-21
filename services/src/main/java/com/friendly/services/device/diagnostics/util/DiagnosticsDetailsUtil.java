package com.friendly.services.device.diagnostics.util;

import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.device.diagnostics.orm.acs.repository.DeviceDiagnosticsRepository;
import com.google.common.base.CharMatcher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DiagnosticsDetailsUtil {

    @NonNull
    private final DeviceDiagnosticsRepository deviceDiagnosticsRepository;
    
    public DiagnosticDetail getDiagnosticParam(final Long id, final DiagnosticParam diagnosticParam) {
        String paramName = diagnosticParam.getParamName();
        String name = diagnosticParam.getName();
        List<String> valueParam = deviceDiagnosticsRepository.getDiagnosticGetParams(id, "%." + paramName);
        if (valueParam.isEmpty() || StringUtils.isBlank(valueParam.get(0))) {
            valueParam = deviceDiagnosticsRepository.getDiagnosticSetParams(id, "%." + paramName);
        }

        String paramValue = null;
        if (!valueParam.isEmpty() && valueParam.get(0) != null) {
            if ("EnableIndividualPacketResults".equals(paramName)) {
                paramValue = "1".equals(valueParam.get(0)) ? "true" : "false";
            } else {
                paramValue = valueParam.get(0);
            }
        }

        return new DiagnosticDetail(paramName, paramValue, name);
    }

    public List<Map<String, String>> getDiagnosticParamResult(final Long id, final String resultFilter) {
        final List<Map<String, String>> results = deviceDiagnosticsRepository.getDiagnosticResults(id, resultFilter);

        final Map<Integer, List<Map<String, String>>> mapByInterface =
                results.stream()
                        .filter(map -> StringUtils.isNotBlank(CharMatcher.inRange('0', '9')
                                .retainFrom(map.get("name"))))
                        .collect(Collectors.groupingBy(map -> NumberUtils.createInteger(
                                CharMatcher.inRange('0', '9').retainFrom(map.get("name")))));

        return mapByInterface.keySet()
                .stream()
                .filter(Objects::nonNull)
                .map(key -> mapByInterface.get(key)
                        .stream()
                        .collect(Collectors.toMap(map -> StringUtils.substringAfterLast(
                                        map.get("name"), "."),
                                map -> map.get("value").trim())))
                .collect(Collectors.toList());
    }

    public List<Object[]> getTraceDiagnosticResults(final Long id) {
        return DbConfig.isOracle()
                ? deviceDiagnosticsRepository. getTraceDiagnosticResultsOracle(id)
                : deviceDiagnosticsRepository.getTraceDiagnosticResultsMysql(id);
    }

    public static void calculateDetails(final List<DiagnosticDetail> details, final String start, final String finish,
                                        final String testBytes, final String totalBytes, TransferType transferType) {
        if (StringUtils.isBlank(finish) || StringUtils.isBlank(start)) {
            return;
        }
        BigDecimal fileSizeB = null;
         if (StringUtils.isNotBlank(totalBytes)) {
             fileSizeB = new BigDecimal(totalBytes);
             final BigDecimal fileSizeMb =
                     fileSizeB.divide(BigDecimal.valueOf(1024), 4, RoundingMode.HALF_UP)
                             .divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP);
             details.add(new DiagnosticDetail(transferType.getSize(), fileSizeMb.toString(), transferType.getName()));
         } else if (StringUtils.isNotBlank(testBytes)) {
             fileSizeB = new BigDecimal(testBytes);
             final BigDecimal fileSizeMb =
                     fileSizeB.divide(BigDecimal.valueOf(1024), 4, RoundingMode.HALF_UP)
                             .divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP);
             details.add(new DiagnosticDetail(transferType.getSize(), fileSizeMb.toString(), transferType.getName()));
         }

        Timestamp startTime = getTimeValue(start);
        Timestamp finishTime = getTimeValue(finish);

        if(startTime == null || finishTime == null) {
            return;
        }

        final long timeSec = ChronoUnit.SECONDS.between(startTime.toLocalDateTime(), finishTime.toLocalDateTime());
        final BigDecimal timeMs = new BigDecimal(ChronoUnit.NANOS.between(
                startTime.toLocalDateTime(), finishTime.toLocalDateTime())).divide(new BigDecimal(100_000_0), 3, RoundingMode.HALF_UP);

        details.add(new DiagnosticDetail(transferType.getTime(), String.valueOf(timeSec), transferType.getName()));

        if (fileSizeB != null) {
            BigDecimal mbSize = new BigDecimal(1000 * 1000);
            final BigDecimal speed = timeMs.compareTo(BigDecimal.ZERO) <= 0
                    ? new BigDecimal(0)
                    : fileSizeB.multiply(new BigDecimal(8))
                    .divide(mbSize, 10, RoundingMode.HALF_UP)
                    .divide(timeMs, 2, RoundingMode.HALF_UP);
            details.add(new DiagnosticDetail(transferType.getSpeed(), speed.toString(), transferType.getName()));
        }
    }

    public static Timestamp getTimeValue(String val) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                .optionalEnd()
                .optionalStart()
                .appendOffset("+HH:MM", "Z")
                .optionalEnd()
                .toFormatter();
        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(val, new ParsePosition(0)));
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.from(instant);
    }
}
