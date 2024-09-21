package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Created by alexandr.kaygorodov (12.11.2020)
 * */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CpeTaskEvent implements Event {

    private Integer taskId;
    private TaskState taskState;
    private String taskName;
    private String typeName;
    private Long taskKey;
    private Integer cpeId;
    private Long transactionId;
    private Integer priority;
    private Integer repeats;
    private Integer errorCode;
    private Set<Integer> idsFromTask;
    private String errorMsg;

    public enum TaskState {
        Pending,
        Completed,
        Failed,
        Rejected,
        Processed
    }
}
