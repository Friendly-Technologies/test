package com.friendly.services.infrastructure.utils;

import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@RequiredArgsConstructor
@Builder
public class DateHelper {
    final Session session;
    final UserResponse user;
    static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .optionalStart()
            .appendOffset("+HH:MM", "Z")
            .optionalEnd()
            .toFormatter();

    public static Instant uiTimeToInstant(String period, String zoneId) {
        LocalDateTime localDateTime = LocalDateTime.parse(period, formatter);
        return localDateTime.atZone(ZoneId.of(zoneId)).toInstant();
    }

    public String convertToZoneTime(Instant acsTime) {
        return DateTimeUtils.formatAcs(acsTime, session.getClientType(),
                session.getZoneId(), user.getDateFormat(), user.getTimeFormat());
    }

    public Instant convertToIsoTime(Instant acsTime) {
        return DateTimeUtils.serverToUtc(acsTime, session.getClientType());
    }

    public static Instant periodToInstant(int hour, int minute) {
        return Instant.now().atZone(ZoneOffset.UTC)
                .withHour(hour)
                .withMinute(minute)
                .toInstant();
    }
}