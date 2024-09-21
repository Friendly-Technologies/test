package com.friendly.services.infrastructure.utils.websocket;

import com.friendly.services.infrastructure.utils.websocket.model.CpeCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Processor to interact with Web Socket
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Component
@RequiredArgsConstructor
public class WsProcessor {

    public void processCpeCreatedEvent(final CpeCreatedEvent event){

    }

}