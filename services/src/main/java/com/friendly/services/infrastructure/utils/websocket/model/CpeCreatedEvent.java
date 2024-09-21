package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpeCreatedEvent implements Event {

    private Integer cpeId;
    private String cpeSerial;
    private Integer locationId;
    private Integer protocolId;

}
