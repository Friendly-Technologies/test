package com.friendly.services.settings.sessions;

import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.ActiveSessionsBody;
import com.friendly.commons.models.reports.SessionStatisticsBody;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserSession;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.settings.sessions.orm.iotw.model.SessionEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.sessions.orm.iotw.repository.SessionRepository;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.sessions.mapper.SessionsMapper;
import com.friendly.services.uiservices.user.UserServiceHelper;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.commons.models.websocket.ActionType.DELETE;
import static com.friendly.commons.models.websocket.SettingType.SESSIONS;

/**
 * Service that exposes the base functionality for interacting with {@link Session} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    @NonNull
    private final UserServiceHelper userServiceHelper;
    @NonNull
    private final SessionRepository sessionRepository;
    @NonNull
    private final SessionsMapper sessionsMapper;
    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final DomainService domainService;
    @NonNull
    private final WsSender wsSender;

    /**
     * Create Session
     * USE ONLY FOR AUTH
     */
    public Session createOrUpdateSession(final Session session) {
        final UserEntity user = userServiceHelper.getUser(session.getUserId());
        final SessionEntity sessionEntity = sessionRepository.saveAndFlush(
                sessionsMapper.sessionToSessionEntity(session));
        return sessionsMapper.sessionEntityToSession(sessionEntity, user.getDateFormat(), user.getTimeFormat());
    }

    /**
     * Get session
     * USE ONLY FOR AUTH
     */
    public Session getSession(final String sessionHash) {
        return sessionRepository.getActiveSession(sessionHash, Instant.now())
                .map(s -> {
                    final UserEntity user = userServiceHelper.getUser(s.getUserId());
                    return sessionsMapper.sessionEntityToSession(s, user.getDateFormat(),
                            user.getTimeFormat());
                })
                .orElse(null);
    }

    /**
     * Delete session
     * USE ONLY FOR AUTH
     */
    public void killSession(final String sessionHash) {
        sessionRepository.killSession(sessionHash, Instant.now());
    }

    public void killSessions(final List<String> sessionHash) {
        sessionRepository.killSessions(sessionHash, Instant.now());
    }

    /**
     * Get user Session
     */
    public FTPage<UserSession> getActiveSessions(final String token, final ActiveSessionsBody body) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserEntity user = userServiceHelper.getUser(session.getUserId());
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "lastActivity");

        final List<Long> userIds = userServiceHelper.getUserIdsByDomainId(session.getClientType(), body.getDomainId());
        final List<Page<Object[]>> sessionEntityPage =
                pageable.stream()
                        .map(p -> userIds == null ? sessionRepository.getActiveSessions(Instant.now(), clientType, p) :
                                sessionRepository.getActiveSessions(userIds, Instant.now(), p))
                        .collect(Collectors.toList());
        final List<UserSession> userSessions =
                sessionEntityPage.stream()
                        .map(Page::getContent)
                        .flatMap(s -> sessionsMapper.activeSessionsToUserSessions(s, session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList());
        userSessions.forEach(userSession -> userSession.setDomain(
                domainService.getDomainNameById(userSession.getDomainId())));

        return buildUserSessionPage(sessionEntityPage, userSessions);
    }

    public List<String> getActiveSessionHashes(final String token, final List<Long> ids) {
        jwtService.getSession(token);
        return ids == null ? new ArrayList<>() : sessionRepository.getActiveSessionHashes(ids, Instant.now());
    }

    /**
     * Get Session Statistic
     */
    public FTPage<UserSession> getSessionStatistic(final String token,
                                                   final SessionStatisticsBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "lastActivity");
        final ClientType clientType = jwtService.getClientTypeByHeaderAuth(token);

        final List<Long> userIds = userServiceHelper.getUserIdsByDomainId(clientType, body.getDomainId());
        final List<Page<Object[]>> sessionEntityPage =
                pageable.stream()
                        .map(p -> userIds == null || userIds.isEmpty() ? sessionRepository.getSessionStatistic(body.getFrom(), body.getTo(), p) :
                                sessionRepository.getSessionStatistic(userIds, body.getFrom(), body.getTo(), p))
                        .collect(Collectors.toList());
        final List<UserSession> userSessions =
                sessionEntityPage.stream()
                        .map(Page::getContent)
                        .flatMap(s -> sessionsMapper.sessionsToSessionsStatistics(s).stream())
                        .collect(Collectors.toList());
        userSessions.forEach(userSession -> userSession.setDomain(
                domainService.getDomainNameById(userSession.getDomainId())));

        return buildUserSessionPage(sessionEntityPage, userSessions);
    }

    private FTPage<UserSession> buildUserSessionPage(
            final List<Page<Object[]>> pageList,
            final List<UserSession> userSessions) {
        final FTPage<UserSession> userSessionPage = new FTPage<>();

        return userSessionPage.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(pageList))
                .items(userSessions)
                .build();
    }

    /**
     * Delete sessions by SessionHash
     */
    @Transactional
    public void killSessionsByHash(final String token, final List<String> sessionHashes) {
        final ClientType clientType = jwtService.getClientTypeByHeaderAuth(token);

        sessionHashes.forEach(
                sessionHash -> sessionRepository.findById(sessionHash)
                        .ifPresent(entity -> killSessionAndNotify(clientType, sessionHash,
                                entity.getNotificationIdentifier())));
    }

    public void killSessionAndNotify(final ClientType clientType,
                                        final String sessionHash,
                                        final String notificationIdentifier) {
        sessionRepository.killSession(sessionHash, Instant.now());
        wsSender.sendLogoutEvent(notificationIdentifier);
        wsSender.sendSettingEvent(clientType, DELETE, SESSIONS, sessionHash);
    }

    public void killSessionsByUserExpireTime() {
        List<Long> ids = sessionRepository.getActiveSession(Instant.now()).stream()
                .map(s -> userServiceHelper.getUser(s.getUserId()))
                .filter(u -> isExpired(u.getExpireDate()))
                .map(AbstractEntity::getId)
                .collect(Collectors.toList());
        sessionRepository.deleteAllByUserId(ids, Instant.now());
    }

    public void killSessionsByUserId(Long id) {
        sessionRepository.deleteAllByUserId(id, Instant.now());
    }

    public boolean isExpired(Instant expiredDateIso) {
        if (expiredDateIso == null) {
            return false;
        }
        return ChronoUnit.DAYS.between(Instant.now(), expiredDateIso.plus(1, ChronoUnit.DAYS)) < 0;
    }

}
