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
public class DownloadFileCreatedEvent implements Event {

    private Integer id;
    private Integer cpeId;
    private Timestamp created;
    private String creator;
    private Timestamp updated;
    private String updator;
    private Integer reprovision;
    private Integer priority;
    private String filename;
    private Integer fileSize;
    private String targetFileName;
    private Integer delaySeconds;
    private String successURL;
    private String failureURL;
    private String url;
    private String username;
    private String password;
    private Integer fileTypeId;
    private Boolean resetSession;
    private Boolean sendBytes;
    private String fileVersion;
    private Integer deliveryMethod;
    private Integer deliveryProtocol;

}
