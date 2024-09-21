package com.friendly.services.uiservices.auth;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.exceptions.FriendlyUnauthorizedException;
import com.friendly.commons.exceptions.FriendlyUnauthorizedUserException;
import com.friendly.commons.models.auth.request.AuthBody;
import com.friendly.commons.models.auth.request.AuthRequest;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.auth.response.LogoutReasonTypeResponse;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.settings.config.AbstractConfigItem;
import com.friendly.commons.models.settings.config.IntegerItem;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.infrastructure.config.jpa.DBContextHolder;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.UserLogRepository;
import com.friendly.services.settings.sessions.SessionService;
import com.friendly.services.settings.userinterface.InterfaceService;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.LicenseUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.friendly.commons.models.auth.LogoutReasonType.LOGOUT;
import static com.friendly.commons.models.reports.UserActivityType.PASSWORD_CHANGE;
import static com.friendly.commons.models.reports.UserActivityType.USER_LOGIN;
import static com.friendly.commons.models.reports.UserActivityType.USER_LOGOUT;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DENIED_DOMAIN;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FAILED_ATTEMPTS;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.INVALID_TOKEN;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PERMISSION_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.RESET_PASSWORD_EXPIRED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.RESTORE_PASSWORD_FAILED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.SESSION_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.UNSUPPORTED_AUTH_METHOD;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USER_BLOCKED_TIME_EXPIRED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.WRONG_PASSWORD;
import static com.friendly.services.settings.userinterface.InterfaceItem.MAX_FAILED_LOGIN;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtService {


    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${jwt.token.expired}")
    private long expireTimeInMilliseconds;

    @NonNull
    private final UserService userService;
    @NonNull
    private final SessionService sessionService;
    @NonNull
    private final InterfaceService interfaceService;
    @NonNull
    private final StatisticService statisticService;
    @NonNull
    private final PasswordEncoder passwordEncoder;
    @NonNull
    private final AuthenticationManager authenticationManager;
    @NonNull
    private final UserLogRepository userLogRepository;

    private static final int DEFAULT_MAX_FAILED_LOGIN = 5;

    public JwtService(@Lazy final UserService userService,
                      @Lazy final SessionService sessionService,
                      @Lazy final InterfaceService interfaceService,
                      final StatisticService statisticService,
                      final PasswordEncoder passwordEncoder,
                      final AuthenticationManager authenticationManager,
                      final UserLogRepository userLogRepository) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.interfaceService = interfaceService;
        this.statisticService = statisticService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userLogRepository = userLogRepository;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public AuthBody createToken(final AuthRequest request,
                                final HttpSession httpSession,
                                final String notificationIdentifier,
                                final HttpServletRequest httpServletRequest,
                                final AuthType authType) {
        final ClientType clientType = request.getClientType();
        UserResponse user = new UserResponse();
        final Instant now = Instant.now();
        final ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(request.getTimeZoneOffsetMin() != null
                ? request.getTimeZoneOffsetMin() * 60 : 0);

        switch (authType) {
            case DATABASE:
                return getAuthBodyForDatabase(request, httpSession, notificationIdentifier, httpServletRequest, user, clientType, zoneOffset, now);
            case LDAP:
                return getAuthBodyForLdap(request, httpSession, notificationIdentifier, httpServletRequest, user, clientType, now, zoneOffset);
            case SAML:
            case WINDOWS:
                return getAuthBodyForWindows(request, httpSession, notificationIdentifier, httpServletRequest, user, clientType, now, zoneOffset);
            default:
                throw new FriendlyIllegalArgumentException(UNSUPPORTED_AUTH_METHOD);
        }
    }

    private int getMaxFailedLogin(ClientType clientType) {
        Optional<String> maxFailedLogin = interfaceService.getInterfaceValue(clientType,
                MAX_FAILED_LOGIN.getValue());

        return maxFailedLogin.map(Integer::valueOf)
                .orElseGet(() -> {
                    log.warn("Value not found for {}. Using default value: {}", MAX_FAILED_LOGIN.getValue(),
                            DEFAULT_MAX_FAILED_LOGIN);
                    return DEFAULT_MAX_FAILED_LOGIN;
                });
    }

    private AuthBody getAuthBodyForDatabase(AuthRequest request, HttpSession httpSession, String notificationIdentifier,
                                            HttpServletRequest httpServletRequest, UserResponse user,
                                            ClientType clientType, ZoneOffset zoneOffset, Instant now) {
        if (request.getUsername() != null) {
            user = getUserResponse(request, httpServletRequest, clientType);
        }
        if (request.getResetPasswordKey() != null && !request.getResetPasswordKey().isEmpty()) {
            user = resetPassword(request, httpServletRequest, user, zoneOffset, clientType);
        } else {
            final int maxFailedLogin = getMaxFailedLogin(clientType);
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                if (user.getLastLogin() != null && checkTimeout(clientType, user.getLastLogin())
                        && user.getFailedAttempts() == maxFailedLogin - 1) {
                    throw new FriendlyUnauthorizedUserException(FAILED_ATTEMPTS);
                }
                userService.updateUserLoginDetails(user.getId(), 0);
            } else {
                incrementFailedAttemptsOrBlockUser(clientType, user, maxFailedLogin);
            }
        }

        validateDomain(user);

        final String sessionHash = Base64.getEncoder().encodeToString((httpSession.getId() + now).getBytes());
        final Session session = sessionService.createOrUpdateSession(Session.builder()
                .sessionHash(sessionHash)
                .notificationIdentifier(notificationIdentifier)
                .userId(user.getId())
                .clientType(clientType)
                .loggedAtIso(now)
                .lastActivityIso(now)
                .expireTimeIso(now.plusMillis(expireTimeInMilliseconds))
                .zoneId(zoneOffset.getId())
                .build());

        final Claims claims = Jwts.claims()
                .setSubject(session.getSessionHash())
                .setExpiration(Date.from(now.plusMillis(expireTimeInMilliseconds)));
        claims.put("userId", user.getId());
        claims.put("clientType", clientType);
        claims.put("zoneId", zoneOffset.getId());

        return AuthBody.builder()
                .token(Jwts.builder()
                        .setClaims(claims)
                        .signWith(SignatureAlgorithm.HS256, secret)
                        .compact())
                .sessionHash(sessionHash)
                .build();
    }

    private AuthBody getAuthBodyForLdap(AuthRequest request, HttpSession httpSession, String notificationIdentifier,
                                        HttpServletRequest httpServletRequest, UserResponse user, ClientType clientType,
                                        Instant now, ZoneOffset zoneOffset) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (request.getUsername() != null) {
            user = getUserResponse(request, httpServletRequest, clientType);
        }

        final String sessionHash = Base64.getEncoder().encodeToString((httpSession.getId() + now).getBytes());
        final Session session = sessionService.createOrUpdateSession(Session.builder()
                .sessionHash(sessionHash)
                .notificationIdentifier(notificationIdentifier)
                .userId(user.getId())
                .clientType(clientType)
                .loggedAtIso(now)
                .lastActivityIso(now)
                .expireTimeIso(now.plusMillis(expireTimeInMilliseconds))
                .zoneId(zoneOffset.getId())
                .build());

        final Claims claims = Jwts.claims()
                .setSubject(session.getSessionHash())
                .setExpiration(Date.from(now.plusMillis(expireTimeInMilliseconds)));
        claims.put("userId", user.getId());
        claims.put("clientType", clientType);
        claims.put("zoneId", zoneOffset.getId());

        return AuthBody.builder()
                .token(Jwts.builder()
                        .setClaims(claims)
                        .signWith(SignatureAlgorithm.HS256, secret)
                        .compact())
                .sessionHash(sessionHash)
                .build();
    }

    private AuthBody getAuthBodyForWindows(AuthRequest request, HttpSession httpSession, String notificationIdentifier,
                                        HttpServletRequest httpServletRequest, UserResponse user, ClientType clientType,
                                        Instant now, ZoneOffset zoneOffset) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (request.getUsername() != null) {
            user = getUserResponse(request, httpServletRequest, clientType);
        }

        final String sessionHash = Base64.getEncoder().encodeToString((httpSession.getId() + now).getBytes());
        final Session session = sessionService.createOrUpdateSession(Session.builder()
                .sessionHash(sessionHash)
                .notificationIdentifier(notificationIdentifier)
                .userId(user.getId())
                .clientType(clientType)
                .loggedAtIso(now)
                .lastActivityIso(now)
                .expireTimeIso(now.plusMillis(expireTimeInMilliseconds))
                .zoneId(zoneOffset.getId())
                .build());

        final Claims claims = Jwts.claims()
                .setSubject(session.getSessionHash())
                .setExpiration(Date.from(now.plusMillis(expireTimeInMilliseconds)));
        claims.put("userId", user.getId());
        claims.put("clientType", clientType);
        claims.put("zoneId", zoneOffset.getId());

        return AuthBody.builder()
                .token(Jwts.builder()
                        .setClaims(claims)
                        .signWith(SignatureAlgorithm.HS256, secret)
                        .compact())
                .sessionHash(sessionHash)
                .build();
    }

    private UserResponse getUserResponse(AuthRequest request, HttpServletRequest httpServletRequest,
                                         ClientType clientType) {
        UserResponse user;
        String username = request.getUsername();
        String domainName = null;
        if (username.contains("@")) {
            final String[] login = username.split("@");
            username = login[0];
            domainName = login.length > 1 ? login[1] : null;
        }

        DBContextHolder.setCurrentDb(clientType);
        user = userService.getUserByUsernameAndDomain(username, clientType, domainName);
        checkUser(user, username);
        statisticService.addUserLogAct(UserActivityLog.builder()
                .clientType(clientType)
                .userId(user.getId())
                .activityType(USER_LOGIN)
                .note(createNote(request, httpServletRequest, user))
                .build());
        return user;
    }

    private void checkUser(UserResponse user, String username) {
        if (user == null) {
            throw new FriendlyUnauthorizedUserException(WRONG_PASSWORD, username);
        }

        if (isBlocked(user)) {
            throw new FriendlyUnauthorizedUserException(PERMISSION_NOT_FOUND, username);
        }

        if (isExpired(user)) {
            throw new FriendlyUnauthorizedUserException(USER_BLOCKED_TIME_EXPIRED, username);
        }
    }

    private UserResponse resetPassword(AuthRequest request, HttpServletRequest httpServletRequest, UserResponse user,
                                       ZoneOffset zoneOffset, ClientType clientType) {
        final String resetPasswordKey = LicenseUtils.decryptLicense(request.getResetPasswordKey());
        if (resetPasswordKey == null || !resetPasswordKey.contains("\t")) {
            throw new FriendlyUnauthorizedUserException(RESTORE_PASSWORD_FAILED);
        }
        final String[] keyArray = resetPasswordKey.split("\t");
        if (keyArray.length == 2) {
            final long expirationMls = Long.parseLong(keyArray[1]);
            if (System.currentTimeMillis() > expirationMls) {
                final Long userId = Long.parseLong(keyArray[0]);
                userService.updateIsChangePasswordFieldForUser(userId, false);
                throw new FriendlyUnauthorizedUserException(RESET_PASSWORD_EXPIRED);
            } else {
                final long userId = Long.parseLong(keyArray[0]);
                user = userService.getUserByIdWithoutDomain(userId, zoneOffset.getId());
                userService.updateIsChangePasswordFieldForUser(userId, true);

                statisticService.addUserLogAct(UserActivityLog.builder()
                        .clientType(clientType)
                        .userId(user.getId())
                        .activityType(PASSWORD_CHANGE)
                        .note(createNote(request, httpServletRequest, user))
                        .build());
            }
        }
        return user;
    }

    private void incrementFailedAttemptsOrBlockUser(ClientType clientType, UserResponse user, int maxFailedLogin) {
        if (user.getFailedAttempts() < maxFailedLogin - 1) {
            userService.updateUserLoginDetails(user.getId(), user.getFailedAttempts() + 1);
        } else {
            if (checkTimeout(clientType, user.getLastLogin())) {
                throw new FriendlyUnauthorizedUserException(FAILED_ATTEMPTS);
            } else {
                userService.updateUserLoginDetails(user.getId(), 1);
            }
        }
        throw new FriendlyUnauthorizedUserException(WRONG_PASSWORD);
    }

    private String createNote(AuthRequest request, HttpServletRequest httpServletRequest, UserResponse user) {
        return String.format("UserGroup=%s; TimeZone=%s min; IP=%s; Failed attempt=%s",
                user.getUserGroup().getName(), request.getTimeZoneOffsetMin(), httpServletRequest.getRemoteAddr(),
                user.getFailedAttempts());
    }

    private boolean checkTimeout(ClientType clientType, Instant lastLogin) {
        long timeout = interfaceService.getInterfaceValue(clientType, "DisableUserPeriodOnAttemptsExceed")
                .map(Long::valueOf)
                .orElseThrow(() -> new IllegalStateException("Value not found for DisableUserPeriodOnAttemptsExceed"));

        return lastLogin.plusSeconds(timeout).isAfter(Instant.now());
    }

    public Long getUserIdByHeaderAuth(final String auth) {
        return getSession(auth).getUserId();
    }

    public ClientType getClientTypeByHeaderAuth(final String auth) {
        return getSession(auth).getClientType();
    }

    public Session getSession(final String auth) {
        return getSessionHash(auth).map(sessionService::getSession)
                .filter(this::validateSession)
                .map(this::updateSession)
                .orElseThrow(() -> new FriendlyUnauthorizedUserException(INVALID_TOKEN));
    }


    public LogoutReasonTypeResponse logout(final String token, HttpServletRequest httpServletRequest) {
        try {
            Optional<String> sessionHash = getSessionHash(token);
            if (sessionHash.isPresent()) {
                return processLogout(sessionHash.get(), httpServletRequest);
            } else {
                throw new FriendlyEntityNotFoundException(SESSION_NOT_FOUND);
            }
        } catch (ExpiredJwtException expiredJwtException) {
            String sessionHash = getSessionHashFromExpiredToken(expiredJwtException);
            processLogout(sessionHash, httpServletRequest);
            throw new FriendlyUnauthorizedException(INVALID_TOKEN);
        }
    }

    private LogoutReasonTypeResponse processLogout(String sessionHash, HttpServletRequest httpServletRequest) {
        Session session = sessionService.getSession(sessionHash);
        sessionService.killSession(sessionHash);
        if (session != null) {
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .clientType(session.getClientType())
                    .userId(session.getUserId())
                    .activityType(USER_LOGOUT)
                    .note(String.format("IP=%s;", httpServletRequest.getRemoteAddr()))
                    .build());
        }
        return new LogoutReasonTypeResponse(LOGOUT);
    }


    private boolean validateSession(final Session session) {
        if (validateDomain(userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId()))) {
            return true;
        } else {
            sessionService.killSessionAndNotify(session.getClientType(), session.getSessionHash(),
                    session.getNotificationIdentifier());
        }
        return false;
    }

    private boolean validateDomain(final UserResponse user) {
        final AbstractConfigItem interfaceItem = interfaceService.getInterfaceItem(user.getId(), "IspEnable", false);
        if (interfaceItem != null) {
            final IntegerItem domainsEnable = (IntegerItem) interfaceItem;
            if (Objects.equals(domainsEnable.getValue(), 0) && user.getDomainId() != null && user.getDomainId() != 0) {
                throw new FriendlyUnauthorizedUserException(DENIED_DOMAIN);
            }
        }
        return true;
    }

    private Optional<String> getSessionHash(final String auth) {
        return Optional.ofNullable(auth)
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7))
                .map(this::getSessionHashByToken);
    }

    private String getSessionHashByToken(final String token) {
        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        final ClientType clientType = ClientType.valueOf((String) claims.get("clientType"));
        DBContextHolder.setCurrentDb(clientType);

        return claims.getSubject();
    }

    private String getSessionHashFromExpiredToken(final ExpiredJwtException expiredJwtException) {
        final Claims claims = expiredJwtException.getClaims();
        final ClientType clientType = ClientType.valueOf((String) claims.get("clientType"));
        DBContextHolder.setCurrentDb(clientType);
        return claims.getSubject();
    }

    private Session updateSession(final Session session) {
        final Instant now = Instant.now();
        return sessionService.createOrUpdateSession(session.toBuilder()
                .lastActivityIso(now)
                .expireTimeIso(now.plusMillis(expireTimeInMilliseconds))
                .build());
    }

    private boolean isBlocked(UserResponse user) {
        return user.getBlocked() != null && user.getBlocked();
    }

    private boolean isExpired(UserResponse user) {
        return sessionService.isExpired(user.getExpireDateIso());
    }

}