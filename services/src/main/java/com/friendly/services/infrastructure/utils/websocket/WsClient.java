package com.friendly.services.infrastructure.utils.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsClient implements InitializingBean {

    private final static String URL_WS = "ws://localhost:8025/ws/endpoint";
    private final static String URL_WSS = "wss://localhost:4430/ws/endpoint";

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final WsProcessor processor;

    @Override
    public void afterPropertiesSet() {
        final Thread thread = new Thread(this::initWebSocketConnect);
        thread.start();
    }

    private void initWebSocketConnect() {

        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        final WebSocketClient client = new SockJsClient(transports);
        final WebSocketStompClient stompClient = new WebSocketStompClient(client);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        final StompSessionHandler sessionHandler = new WsSessionHandler(mapper, processor);
        try {
            final ListenableFuture<StompSession> stompSessionFuture = stompClient.connect(URL_WS, sessionHandler);
            stompSessionFuture.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Can not connect to ACS WebSocket Service");
            return;
        }

        new Scanner(System.in).nextLine(); // Don't close immediately.
    }
}
