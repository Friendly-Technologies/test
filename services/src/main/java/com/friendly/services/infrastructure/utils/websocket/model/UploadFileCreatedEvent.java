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
public class UploadFileCreatedEvent implements Event {

    private Integer id;
    private Long taskId;
    private Integer cpeId;
    private Timestamp created;
    private String creator;
    private Timestamp updated;
    private String updator;
    private String filename;
    private Integer delaySeconds;
    private String url;
    private String username;
    private String password;
    private Integer fileTypeId;
    private Integer instance;
    private Integer fileSize;
    private String targetFileName;

}
