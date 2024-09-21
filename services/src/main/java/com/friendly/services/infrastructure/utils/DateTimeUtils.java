package com.friendly.services.infrastructure.utils;

import com.friendly.commons.models.auth.ClientType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@UtilityClass
public class DateTimeUtils {

    private static final Integer ACS_SHIFT = 3;
    private static final String DEFAULT = "Default";
    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private static final String UTC = "UTC";
    private static final Map<ClientType, Integer> OFFSET_SEC_MAP = new EnumMap<>(ClientType.class);

    public static void setTimeZone(final ClientType clientType, final ZoneOffset zoneOffset) {
        OFFSET_SEC_MAP.put(clientType, zoneOffset.getTotalSeconds());
    }

    public static String convertTimeToIso(final Instant instant, String zoneId) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.of(zoneId)).toLocalDateTime().toString() + "Z";
    }

    public static Instant parse(final String dateTime, final String zoneId, final String dateFormat,
                                final String timeFormat) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }
        final DateTimeFormatter dateTimeFormatter = getDateTimeFormatter(dateFormat, timeFormat, ZoneId.of(zoneId));
        return ZonedDateTime.parse(dateTime, dateTimeFormatter).toInstant();
    }

    public static String format(final Instant instant, final String zoneId, final String dateFormat,
                                final String timeFormat) {
        if (instant == null) {
            return null;
        }
        return getDateTimeFormatter(dateFormat, timeFormat, ZoneId.of(zoneId)).format(instant);
    }

    public static String formatAcs(final Instant instant, final ClientType clientType, final String zoneId,
                                   final String dateFormat, final String timeFormat) {
        if (instant == null) {
            return null;
        }
        ZoneId zoneIdOffset = getZoneIdOffset(clientType, zoneId);

        return getDateTimeFormatter(dateFormat, timeFormat, zoneIdOffset).format(instant);
    }

    public static String formatAcsWithDate(final Instant instant, final ClientType clientType, final String zoneId,
                                           final String dateFormat) {
        if (instant == null) {
            return null;
        }
        ZoneId zoneIdOffset = getZoneIdOffset(clientType, zoneId);

        return getFormatterForDate(dateFormat, zoneIdOffset).format(instant);
    }

    public static String formatAcsWithTime(final Instant instant, final ClientType clientType, final String zoneId,
                                           final String timeFormat) {
        if (instant == null) {
            return null;
        }
        ZoneId zoneIdOffset = getZoneIdOffset(clientType, zoneId);

        return getFormatterForTime(timeFormat, zoneIdOffset).format(instant);
    }

    public static Instant serverToUtc(final Instant serverInstant, final ClientType clientType) {
        int serverZoneIdInSeconds = OFFSET_SEC_MAP.get(clientType);
        return serverInstant.minusSeconds(serverZoneIdInSeconds);
    }

    public static Instant utcToServer(final Instant serverInstant, final ClientType clientType) {
        int serverZoneIdInSeconds = OFFSET_SEC_MAP.get(clientType);
        return serverInstant.plusSeconds(serverZoneIdInSeconds);
    }

    public static Instant serverToClient(final Instant serverInstant, final ClientType clientType, String clientZoneId) {
        int zoneId = convertZoneIdServerToClient(clientType, clientZoneId);
        return serverInstant.plusSeconds(zoneId);
    }

    public static Instant clientToServer(final Instant clientInstant, final ClientType clientType, String clientZoneId) {
        int zoneId = convertZoneIdClientToServer(clientType, clientZoneId);
        return clientInstant.plusSeconds(zoneId);
    }

    public static Instant addZoneId(final Instant instant, String zoneId) {
        return instant.plusSeconds(zoneIdToSeconds(zoneId));
    }

    private static ZoneId getZoneIdOffset(ClientType clientType, String zoneId) {
        return StringUtils.isBlank(zoneId)
                ? ZoneId.of(UTC)
                : secondsToZoneId(convertZoneIdServerToClient(clientType, zoneId));
    }

    private static int convertZoneIdServerToClient(final ClientType clientType, String clientZoneId) {
        int serverZoneIdInSeconds = OFFSET_SEC_MAP.get(clientType);
        int clientZoneIdInSeconds = zoneIdToSeconds(clientZoneId);
        return clientZoneIdInSeconds - serverZoneIdInSeconds;
    }

    private static int convertZoneIdClientToServer(final ClientType clientType, String clientZoneId) {
        int clientZoneIdInSeconds = zoneIdToSeconds(clientZoneId);
        int serverZoneIdInSeconds = OFFSET_SEC_MAP.get(clientType);
        return serverZoneIdInSeconds - clientZoneIdInSeconds;
    }

    private static ZoneId secondsToZoneId(int seconds) {
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(seconds);
        return ZoneId.ofOffset(UTC, zoneOffset);
    }

    private static int zoneIdToSeconds(String zoneId) {
        int seconds = 0;
        if (StringUtils.isNotBlank(zoneId)) {
            seconds = ((ZoneOffset) ZoneId.of(zoneId)).getTotalSeconds();
        }
        return seconds;
    }

    private static DateTimeFormatter getFormatterForDate(final String format, final ZoneId zoneId) {
        return getFormatter(format, DEFAULT_DATE_FORMAT, zoneId);
    }

    private static DateTimeFormatter getFormatterForTime(final String format, final ZoneId zoneId) {
        return getFormatter(format, DEFAULT_TIME_FORMAT, zoneId);
    }

    private static DateTimeFormatter getFormatter(final String format, String defaultFormat, final ZoneId zoneId) {
        return DateTimeFormatter.ofPattern(getFormat(format, defaultFormat)).withZone(zoneId);
    }

    private static DateTimeFormatter getDateTimeFormatter(final String dateFormat, final String timeFormat,
                                                          final ZoneId zoneId) {
        return getDateTimeFormatter(dateFormat, timeFormat).withZone(zoneId);
    }
    public static DateTimeFormatter getDefaultDateTimeFormatter() {
        return getDateTimeFormatter(null, null);
    }
    private static DateTimeFormatter getDateTimeFormatter(final String dateFormat, final String timeFormat) {
        String formattedDateFormat = getFormat(dateFormat, DEFAULT_DATE_FORMAT);
        String formattedTimeFormat = getFormat(timeFormat, DEFAULT_TIME_FORMAT);
        String pattern = formattedDateFormat + " " + formattedTimeFormat;

        return DateTimeFormatter.ofPattern(pattern);
    }

    private static String getFormat(String format, String defaultFormat) {
        return StringUtils.defaultIfBlank(format, DEFAULT).equals(DEFAULT)
                ? defaultFormat
                : format;
    }

    public static String revertZoneId(String zoneId) {
        if (zoneId == null || zoneId.isEmpty()) {
            return zoneId;
        }

        if (zoneId.startsWith("-")) {
            return "+" + zoneId.substring(1);
        } else if (zoneId.startsWith("+")){
            return "-" + zoneId.substring(1);
        }
        return zoneId;
    }

    public static Instant convertServerDateToIso(Instant date) {
        return date.minus(ACS_SHIFT, ChronoUnit.HOURS);
    }

    public static Instant convertIsoDateToServer(Instant date) {
        return date.plus(ACS_SHIFT, ChronoUnit.HOURS);
    }

    public static Instant getInstantForClient(Instant date, String zoneId) {
        String format = DateTimeUtils.format(DateTimeUtils.convertIsoDateToServer(date),
                DateTimeUtils.revertZoneId(zoneId), "Default", "Default");
        return convertStringToInstant(format);
    }

    public static Instant convertStringToInstant(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(format, formatter);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    public static Long getDiffMinutes(String zoneId) {
        if(zoneId.equals("Z")) {
            return (long) -ACS_SHIFT * 60;
        }
        char sign = zoneId.charAt(0);
        return Long.parseLong(String.valueOf(zoneId.charAt(1))) * 10
                        + Long.parseLong(String.valueOf(zoneId.charAt(2))) * 60 * (sign == '+' ? 1 : -1)
                - ACS_SHIFT * 60;
    }


    public static XMLGregorianCalendar convertInstantToXMLCalendar(Instant instant) {
        GregorianCalendar cal1 = new GregorianCalendar();
        cal1.setTimeInMillis(instant.toEpochMilli());

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal1);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLGregorianCalendar convertIsoToServerXMLCalendar(String date, ClientType clientType, String zoneId) {
        return convertInstantToXMLCalendar(clientToServer(DateHelper.uiTimeToInstant(date, zoneId), clientType, zoneId));
    }
}
