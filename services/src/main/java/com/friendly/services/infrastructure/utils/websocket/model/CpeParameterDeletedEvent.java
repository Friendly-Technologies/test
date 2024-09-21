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
public class CpeParameterDeletedEvent implements Event {

    private Integer cpeId;

    /* One of these fields is optional */
    private Integer paramNameId;
    private String paramName;

}
