package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Created by alexandr.kaygorodov (12.11.2020)
 * */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CpeObjectCreatedEvent implements Event {

    private Integer id;
    private Integer cpeId;
    private Integer reprovision;
    private Integer priority;
    private Timestamp created;
    private String creator;
    private Timestamp updated;
    private String updator;
    private Integer nameId;
    private Integer copies;
    private Integer parentId;
    private Integer parentInstance;

}
