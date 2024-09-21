package com.friendly.services.infrastructure.utils.websocket.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class CpeParameterCreatedEvent extends CpeParameterChangedEvent {

    public CpeParameterCreatedEvent(Integer cpeId, Integer paramNameId, String value, Boolean writable, String identifier) {
        super(cpeId, paramNameId, value, writable, identifier);
    }

    public CpeParameterCreatedEvent(Integer cpeId, String cpeParameterName, String value, Boolean writable, String identifier) {
        super(cpeId, cpeParameterName, value, writable, identifier);
    }
}
