package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
import com.friendly.services.infrastructure.utils.websocket.model.base.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by alexandr.kaygorodov (12.11.2020)
 * */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IspEvent implements Event {

    private EventType eventType;
    private List<IspItem> isps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IspItem {
        private Integer id;
        private String name;
    }

}
