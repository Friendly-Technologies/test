package com.friendly.services.settings.sessions.mapper;

import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserSession;
import com.friendly.services.settings.sessions.orm.iotw.model.SessionEntity;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionsMapper {

    public Session sessionEntityToSession(final SessionEntity sessionEntity,
                                          final String dateFormat, final String timeFormat) {
        return Session.builder()
                      .sessionHash(sessionEntity.getSessionHash())
                      .userId(sessionEntity.getUserId())
                      .lastActivityIso(sessionEntity.getLastActivity())
                      .loggedAtIso(sessionEntity.getLoggedAt())
                      .expireTimeIso(sessionEntity.getExpireTime())
                      .lastActivity(DateTimeUtils.format(sessionEntity.getLastActivity(), sessionEntity.getZoneId(),
                                                         dateFormat, timeFormat))
                      .loggedAt(DateTimeUtils.format(sessionEntity.getLoggedAt(), sessionEntity.getZoneId(),
                                                     dateFormat, timeFormat))
                      .expireTime(DateTimeUtils.format(sessionEntity.getExpireTime(), sessionEntity.getZoneId(),
                                                       dateFormat, timeFormat))
                      .clientType(sessionEntity.getClientType())
                      .notificationIdentifier(sessionEntity.getNotificationIdentifier())
                      .zoneId(sessionEntity.getZoneId())
                      .build();
    }

    public SessionEntity sessionToSessionEntity(final Session session) {
        return SessionEntity.builder()
                            .sessionHash(session.getSessionHash())
                            .userId(session.getUserId())
                            .lastActivity(session.getLastActivityIso())
                            .loggedAt(session.getLoggedAtIso())
                            .expireTime(session.getExpireTimeIso())
                            .clientType(session.getClientType())
                            .notificationIdentifier(session.getNotificationIdentifier())
                            .zoneId(session.getZoneId())
                            .build();
    }

    public List<UserSession> activeSessionsToUserSessions(final List<Object[]> sessions, final String zoneId,
                                                          final String dateFormat, final String timeFormat) {
        if (sessions == null) {
            return Collections.emptyList();
        }

        return sessions.stream()
                       .map(s -> activeSessionToUserSession(s, zoneId, dateFormat, timeFormat))
                       .collect(Collectors.toList());
    }

    private UserSession activeSessionToUserSession(final Object[] session, final String zoneId,
                                                   final String dateFormat, final String timeFormat) {
        return UserSession.builder()
                          .sessionHash((String) session[0])
                          .username((String) session[1])
                          .domainId((Integer) session[2])
                          .lastActivityIso((Instant) session[3])
                          .loggedAtIso((Instant) session[4])
                          .lastActivity(
                                  DateTimeUtils.format(((Instant) session[3]), zoneId, dateFormat, timeFormat))
                          .loggedAt(DateTimeUtils.format(((Instant) session[4]), zoneId, dateFormat, timeFormat))
                          .build();
    }

    public List<UserSession> activeSessionsToUserSessionList(final List<Object[]> sessions, final String zoneId,
                                                             final String dateFormat, final String timeFormat) {
        if (sessions == null) {
            return Collections.emptyList();
        }

        return sessions.stream()
                       .map(s -> activeSessionToUserSessionList(s, zoneId, dateFormat, timeFormat))
                       .collect(Collectors.toList());
    }

    private UserSession activeSessionToUserSessionList(final Object[] session, final String zoneId,
                                                       final String dateFormat, final String timeFormat) {
        return UserSession.builder()
                          .username((String) session[0])
                          .domain((String) session[1])
                          .lastActivityIso((Instant) session[2])
                          .loggedAtIso((Instant) session[3])
                          .lastActivity(
                                  DateTimeUtils.format(((Instant) session[2]), zoneId, dateFormat, timeFormat))
                          .loggedAt(DateTimeUtils.format(((Instant) session[3]), zoneId, dateFormat, timeFormat))
                          .build();
    }

    public List<UserSession> sessionsToSessionsStatistics(
            final List<Object[]> sessions) {
        if (sessions == null) {
            return Collections.emptyList();
        }
        final Instant now = Instant.now();

        return sessions.stream()
                       .map(session -> sessionToSessionStatistic(session, now))
                       .collect(Collectors.toList());
    }

    private UserSession sessionToSessionStatistic(final Object[] session, final Instant now) {
        final Duration duration = getDuration(session, now, 3, 4);
        final long hours = duration.toHours();
        final long minutes = duration.toMinutes() - (hours * 60);

        return UserSession.builder()
                          .sessionHash((String) session[0])
                          .username((String) session[1])
                          .domainId((Integer) session[2])
                          .duration(String.format("%s h %s m", hours, minutes))
                          .build();
    }

    public List<UserSession> sessionsToSessionsStatisticList(final List<Object[]> sessions, final String zoneId,
                                                             final String dateFormat, final String timeFormat) {
        if (sessions == null) {
            return Collections.emptyList();
        }

        final Instant now = Instant.now();

        return sessions.stream()
                       .map(session -> sessionToSessionStatisticList(session, now, zoneId, dateFormat, timeFormat))
                       .collect(Collectors.toList());
    }

    private UserSession sessionToSessionStatisticList(final Object[] session, final Instant now,
                                                      final String zoneId,
                                                      final String dateFormat, final String timeFormat) {
        final Duration duration = getDuration(session, now, 2, 3);
        final long hours = duration.toHours();
        final long minutes = duration.toMinutes() - (hours * 60);

        return UserSession.builder()
                          .username((String) session[0])
                          .domainId((Integer) session[1])
                          .loggedAtIso((Instant) session[2])
                          .loggedAt(DateTimeUtils.format((Instant) session[2], zoneId, dateFormat, timeFormat))
                          .duration(String.format("%s h %s m", hours, minutes))
                          .build();
    }

    public List<Object[]> sessionsToSessionsStatisticObjects(
            final List<Object[]> sessions) {
        if (sessions == null) {
            return Collections.emptyList();
        }

        return sessions.stream()
                       .map(session -> sessionToSessionStatisticObject(session, Instant.now()))
                       .collect(Collectors.toList());
    }

    private Object[] sessionToSessionStatisticObject(final Object[] session, final Instant now) {
        final Duration duration = getDuration(session, now, 3, 2);
        final long hours = duration.toHours();
        final long minutes = duration.toMinutes() - (hours * 60);

        session[3] = String.format("%s h %s m", hours, minutes);
        return session;
    }

    private Duration getDuration(Object[] session, Instant now, int i, int i2) {
        final Instant loggedTime = (Instant) session[i2];
        final Instant expireTime = (Instant) session[i];
        final Instant minTime = expireTime.compareTo(now) < 0 ? expireTime : now;
        return Duration.between(loggedTime, minTime);
    }

}
