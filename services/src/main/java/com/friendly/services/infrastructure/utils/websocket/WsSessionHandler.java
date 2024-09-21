package com.friendly.services.infrastructure.utils.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.services.infrastructure.utils.websocket.model.CpeCreatedEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.MESSAGE_NOT_SUPPORTED;

@Slf4j
@RequiredArgsConstructor
public class WsSessionHandler extends StompSessionHandlerAdapter {

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final WsProcessor processor;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("New WebSocket session established : " + session.getSessionId());
        session.subscribe("/topic/cpe", this);
        log.info("Subscribed to /topic/cpe");
        session.subscribe("/topic/isp", this);
        log.info("Subscribed to /topic/isp");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                byte[] payload, Throwable exception) {
        log.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        final String payloadType = headers.get("payload-type")
                                          .stream()
                                          .findFirst()
                                          .orElseThrow(() -> new FriendlyIllegalArgumentException(MESSAGE_NOT_SUPPORTED,
                                                                                                  payload.toString()));
        switch (payloadType) {
            case "CpeCreatedEvent":
                processor.processCpeCreatedEvent(mapper.convertValue(payload, CpeCreatedEvent.class));
                break;
        }
    }

}
