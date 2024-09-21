package com.friendly.services.infrastructure.utils.websocket;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.websocket.*;
import com.friendly.services.settings.sessions.orm.iotw.repository.SessionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.friendly.commons.CommonRegistry.*;
import static com.friendly.commons.models.websocket.ActionType.CREATE;
import static com.friendly.commons.models.websocket.ActionType.DELETE;

/**
 * Controller that exposes an API to interact with Web Socket
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Controller
@RequiredArgsConstructor
public class WsSender {

    @NonNull
    private final SessionRepository sessionRepository;

    @NonNull
    private final SimpMessagingTemplate template;

    private final static Map<ClientType, Instant> SYNC_TIME_MAP = new HashMap<>();
    private final static Map<ClientType, List<String>> ACTIVE_SESSION_MAP = new HashMap<>();

    @PostConstruct
    public void init() {
        ACTIVE_SESSION_MAP.clear();
        ACTIVE_SESSION_MAP.put(ClientType.sc, new CopyOnWriteArrayList<>());
        ACTIVE_SESSION_MAP.put(ClientType.mc, new CopyOnWriteArrayList<>());
    }

/*
    @PostConstruct
    public void init() {
        final Map<String, Object> headers = new HashMap<>();
        headers.put("payload-type", "WsMessageLogout");
        final Thread thread = new Thread(() -> test(headers));
        final Thread thread2 = new Thread(() -> test2(headers));
        thread.start();
        thread2.start();
    }

    public void test(final Map<String, Object> headers) {
        while (true) {
            template.convertAndSend("/topic/messages",
                                    WsMessageLogout.builder()
                                                   .text("Session killed")
                                                   .timeIso(Instant.now())
                                                   .build(),
                                    headers);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void test2(final Map<String, Object> headers) {
        while (true) {
            template.convertAndSend("/topic/messages2",
                                    WsMessageLogout.builder()
                                                   .text("Session killed")
                                                   .timeIso(Instant.now())
                                                   .build(),
                                    headers);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
*/

    private void sessionSync(final ClientType clientType) {
        final Instant lastSync = SYNC_TIME_MAP.getOrDefault(clientType, null);
        if (lastSync == null || lastSync.isBefore(Instant.now().minusSeconds(10))) {
            SYNC_TIME_MAP.put(clientType, Instant.now());
            ACTIVE_SESSION_MAP.get(clientType)
                              .addAll(sessionRepository.getActiveNotificationsByClientType(clientType, Instant.now()));
        }
    }

    public void sendLogoutEvent(final String notificationIdentifier) {
        template.convertAndSend(String.format(WS_TOPIC_AUTH, notificationIdentifier),
                                WsMessageLogout.builder()
                                               .text("Session killed")
                                               .timeIso(Instant.now())
                                               .build());
    }

    public void sendCompleteFileEvent(final ClientType clientType, final String link) {
        sessionSync(clientType);
        ACTIVE_SESSION_MAP.get(clientType)
                          .forEach(notificator -> template.convertAndSend(String.format(WS_TOPIC_REPORT, notificator),
                                                                          WsMessageReport.builder()
                                                                                         .actionType(CREATE)
                                                                                         .text("File generated")
                                                                                         .link(link)
                                                                                         .timeIso(Instant.now())
                                                                                         .build()));
    }

    public void sendDeleteFileEvent(final ClientType clientType, final String link) {
        sessionSync(clientType);
        ACTIVE_SESSION_MAP.get(clientType)
                          .forEach(notificator -> template.convertAndSend(String.format(WS_TOPIC_REPORT, notificator),
                                                                          WsMessageReport.builder()
                                                                                         .actionType(DELETE)
                                                                                         .text("File deleted")
                                                                                         .link(link)
                                                                                         .timeIso(Instant.now())
                                                                                         .build()));
    }

    public void sendSettingEvent(final ClientType clientType, final ActionType actionType,
                                 final SettingType settingType, final Object object) {
        sessionSync(clientType);
        ACTIVE_SESSION_MAP.get(clientType)
                          .forEach(notificator -> template.convertAndSend(
                                  String.format(WS_TOPIC_SETTING, notificator)
                                          + settingType.name().toLowerCase(),
                                  WsMessage.builder()
                                           .actionType(actionType)
                                           .object(object)
                                           .timeIso(Instant.now())
                                           .build()));
    }

    public void sendUserEvent(final ClientType clientType, final ActionType actionType, final Object user) {
        sessionSync(clientType);
        ACTIVE_SESSION_MAP.get(clientType)
                          .forEach(notificator -> template.convertAndSend(
                                  String.format(WS_TOPIC_USER, notificator),
                                  WsMessage.builder()
                                           .actionType(actionType)
                                           .object(user)
                                           .timeIso(Instant.now())
                                           .build()));
    }

    public void sendViewEvent(final ClientType clientType, final ActionType actionType, final Object view) {
        sessionSync(clientType);
        ACTIVE_SESSION_MAP.get(clientType)
                          .forEach(notificator -> template.convertAndSend(
                                  String.format(WS_TOPIC_VIEW, notificator),
                                  WsMessage.builder()
                                           .actionType(actionType)
                                           .object(view)
                                           .timeIso(Instant.now())
                                           .build()));
    }

}