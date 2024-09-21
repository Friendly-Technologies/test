package com.friendly.services.settings.emailserver;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.settings.EmailServer;
import com.friendly.commons.models.settings.EmailServers;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.emailserver.orm.iotw.model.EmailServerEntity;
import com.friendly.services.settings.emailserver.orm.iotw.model.EmailServerSpecificEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.emailserver.orm.iotw.repository.EmailServerRepository;
import com.friendly.services.settings.emailserver.orm.iotw.repository.EmailServerSpecificRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserServiceHelper;
import com.friendly.services.infrastructure.utils.PasswordUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.friendly.commons.models.reports.UserActivityType.CONFIGURING_EMAIL;
import static com.friendly.commons.models.websocket.ActionType.CREATE;
import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.commons.models.websocket.SettingType.EMAIL_SERVER;
import static lombok.AccessLevel.PRIVATE;

/**
 * Service that exposes the base functionality for interacting with {@link EmailServer} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailServerService {
    @NonNull
    EmailServerSpecificRepository emailServerSpecificRepository;
    @NonNull
    UserRepository userRepository;
    @NonNull
    UserServiceHelper userServiceHelper;
    @NonNull
    EmailServerRepository emailServerRepository;
    @NonNull
    EmailServerEntityMapper emailServerEntityMapper;
    @NonNull
    JwtService jwtService;
    @NonNull
    WsSender wsSender;
    @NonNull
    StatisticService statisticService;
    @NonNull
    StatisticNoteUtils statisticNoteUtils;

    public EmailServers getEmailServer(String token) {
        ClientSessionInfo sessionInfo = getSessionInfo(token);
        Long userId = sessionInfo.getUserId();
        Integer domainId = userServiceHelper.getDomainId(userId);

        EmailServerEntity defaultServer = emailServerRepository.findById(sessionInfo.getClientType())
                .orElseThrow(() -> new RuntimeException("Not found email server"));

        Optional<EmailServerSpecificEntity> specificServerOpt = Optional.empty();

        if(domainId != 0) {
            specificServerOpt = emailServerSpecificRepository.findByDomainIdAndClientType(domainId,
                    sessionInfo.getClientType());
        }

        return new EmailServers(emailServerEntityMapper.fromEntity(defaultServer),
                specificServerOpt.map(emailServerEntityMapper::fromEntity).orElse(null));
    }

    public EmailServer getEmailServerByClientType(ClientType clientType) {
        EmailServerEntity emailServer = emailServerRepository.findById(clientType)
                .orElseThrow(() -> new RuntimeException("Not found email server"));
        return emailServerEntityMapper.fromEntity(emailServer);
    }

    @Transactional
    public EmailServer updateEmailServer(String token, EmailServer emailServer) {
        ClientSessionInfo sessionInfo = getSessionInfo(token);
        Long userId = sessionInfo.getUserId();

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        Integer domainId = user.getDomainId();
        EmailServer result;

        if (domainId == 0) {
            result = handleDefaultDomain(emailServer, sessionInfo);
        } else {
            result = handleSpecificDomain(emailServer, sessionInfo, domainId);
        }

        return result;
    }

    private EmailServer handleDefaultDomain(EmailServer emailServer, ClientSessionInfo sessionInfo) {
        Optional<EmailServerEntity> optional = emailServerRepository.findById(sessionInfo.getClientType());

        return optional.map(emailServerEntity ->
                updateEmailServer(emailServer, sessionInfo, emailServerEntity))
                .orElseGet(() -> createNewEmailServer(emailServer, sessionInfo.getClientType()));
    }

    private EmailServer updateEmailServer(EmailServer emailServer, ClientSessionInfo sessionInfo, EmailServerEntity existingEntity) {
        EmailServer oldEmailServer = emailServerEntityMapper.fromEntity(existingEntity);
        if (emailServer.equals(oldEmailServer)) {
            return emailServer;
        }

        EmailServer result = emailServerEntityMapper.fromEntity(emailServerRepository.saveAndFlush(
                        emailServerEntityMapper.toEntity(0, sessionInfo.getClientType(), emailServer)));
        wsSender.sendSettingEvent(sessionInfo.getClientType(), UPDATE, EMAIL_SERVER, result);
        StringBuilder note = new StringBuilder("Set");

        appendIfNotEqual(note, "Host", result.getHost(), emailServer.getHost());
        appendIfNotEqual(note, "Port", result.getPort(), emailServer.getPort());
        appendIfNotEqual(note, "Username", result.getUsername(), emailServer.getUsername());
        appendIfNotEqual(note, "Password", PasswordUtils.getHiddenPassword(result.getPassword()),
                emailServer.getPassword());
        appendIfNotEqual(note, "From", result.getFrom(), emailServer.getFrom());
        appendIfNotEqual(note, "Subject", result.getSubject(), emailServer.getSubject());
        appendIfNotEqual(note, "EnableSSL", result.isEnableSSL(), emailServer.isEnableSSL());

        statisticService.addUserLogAct(UserActivityLog.builder()
                .userId(sessionInfo.getUserId())
                .clientType(sessionInfo.getClientType())
                .activityType(CONFIGURING_EMAIL)
                .note(note.toString())
                .build());

        return result;
    }

    private void appendIfNotEqual(StringBuilder note, String field, Object newValue, Object oldValue) {
        if (ObjectUtils.notEqual(newValue, oldValue)) {
            note.append(" ").append(field).append("=").append(newValue).append(";");
        }
    }

    private EmailServer handleSpecificDomain(EmailServer emailServer, ClientSessionInfo sessionInfo, Integer domainId) {
        Optional<EmailServerSpecificEntity> optional = emailServerSpecificRepository.findByDomainIdAndClientType(domainId, sessionInfo.getClientType());

        if (optional.isPresent()) {
            return updateEmailSpecificServer(emailServer, sessionInfo, domainId);
        } else {
            return createNewSpecificEmailServer(emailServer, sessionInfo.getClientType(), domainId);
        }
    }

    private EmailServer updateEmailSpecificServer(EmailServer emailServer, ClientSessionInfo sessionInfo, Integer domainId) {
        Optional<EmailServerSpecificEntity> optional
                = emailServerSpecificRepository.findByDomainIdAndClientType(domainId, sessionInfo.getClientType());
        EmailServer result = null;

        if (optional.isPresent()) {
            EmailServer oldEmailServer = emailServerEntityMapper.fromEntity(optional.get());
            if(isNull(emailServer)) {
                emailServerSpecificRepository.delete(optional.get());
            } else {
                if (emailServer.equals(oldEmailServer)) {
                    return emailServer;
                }
                emailServerSpecificRepository.save(
                        emailServerEntityMapper.toSpecificEntity(optional.get().getId(),
                                domainId, sessionInfo.getClientType(), emailServer));
            }
        } else {
            result = createNewSpecificEmailServer(emailServer, sessionInfo.getClientType(), domainId);

            notifyAboutCreatingNewEmailServer(sessionInfo, result);
        }
        return result;
    }

    private boolean isNull(EmailServer emailServer) {
        return emailServer.getFrom() == null
                && emailServer.getHost() == null
                && emailServer.getSubject() == null
                && emailServer.getPort() == null
                && emailServer.getUsername() == null
                && emailServer.getPassword() == null
                && !emailServer.isEnableSSL();
    }

    private EmailServer createNewSpecificEmailServer(EmailServer emailServer, ClientType clientType, Integer domainId) {
        EmailServerSpecificEntity newEntity = emailServerEntityMapper.toSpecificEntity(null, domainId, clientType, emailServer);

        EmailServerSpecificEntity savedEntity = emailServerSpecificRepository.saveAndFlush(newEntity);

        return emailServerEntityMapper.fromEntity(savedEntity);
    }

    private void notifyAboutCreatingNewEmailServer(ClientSessionInfo sessionInfo,
                                                   EmailServer result) {
        wsSender.sendSettingEvent(sessionInfo.getClientType(), CREATE, EMAIL_SERVER, result);

        String note = statisticNoteUtils.buildNoteCreateNewEmailServer(result);

        statisticService.addUserLogAct(UserActivityLog.builder()
                .userId(sessionInfo.getUserId())
                .clientType(sessionInfo.getClientType())
                .activityType(CONFIGURING_EMAIL)
                .note(note)
                .build());
    }

    private EmailServer createNewEmailServer(EmailServer emailServer, ClientType clientType) {
        EmailServerEntity newEntity = emailServerEntityMapper.toEntity(0, clientType, emailServer);

        EmailServerEntity savedEntity = emailServerRepository.saveAndFlush(newEntity);

        return emailServerEntityMapper.fromEntity(savedEntity);
    }

    private ClientSessionInfo getSessionInfo(String token) {
        Session session = jwtService.getSession(token);
        ClientType clientType = session.getClientType();

        return ClientSessionInfo.builder()
                .clientType(clientType)
                .userId(session.getUserId())
                .build();
    }

}
