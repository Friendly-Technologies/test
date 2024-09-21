package com.friendly.commons.models.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that defines a WebSocket Message
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class WsAbstractMessage implements Serializable {

    private ActionType actionType;
    private Instant timeIso;

}
