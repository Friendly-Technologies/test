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
public class CpeStatusChangedEvent implements Event {

    private Integer cpeProtocolId;
    private Integer cpeId;
    private String cpeSerial;
    public CpeStatus cpeStatus;


    public enum CpeStatus{
        ONLINE, OFFLINE
    }
}