package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
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
public class ReprovisionDeletedEvent implements Event {

    private List<Integer> ids;
    private ReprovisonType reprovisonType;

    public enum ReprovisonType {
        customRPC,
        fileDownload,
        provisionAttribute,
        provisionObject,
        provisionCPE
    }

}
