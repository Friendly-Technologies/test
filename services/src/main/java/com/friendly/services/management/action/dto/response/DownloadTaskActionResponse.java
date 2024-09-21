package com.friendly.services.management.action.dto.response;

import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.DeliveryProtocolType;
import com.friendly.commons.models.device.file.FileActType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadTaskActionResponse extends AbstractActionResponse {
    private Integer delay;
    private String failureUrl;
    private FileActType actType;
    private String filename;
    private Integer fileSize;
    private String password;
    private String successUrl;
    private Boolean isManual = false;
    private String description;
    private String targetFileName;
    private String url;
    private String username;
    private Boolean resetSession;
    private String link;
    private Boolean sendBytes;
    private String version;
    private DeliveryProtocolType deliveryProtocol;
    private DeliveryMethodType deliveryMethod;
    private Boolean newest;
    private Integer fileTypeId;
}
