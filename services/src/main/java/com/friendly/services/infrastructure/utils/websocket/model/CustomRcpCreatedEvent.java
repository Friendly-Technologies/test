package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomRcpCreatedEvent implements Event {

    private Integer id;
    private Integer cpeId;
    private Timestamp created;
    private String creator;
    private Timestamp updated;
    private String updator;
    private Integer priority;
    private Integer reprovision;
    private String methodName;
    private String requestMessage;
    private String responseMessage;
}
