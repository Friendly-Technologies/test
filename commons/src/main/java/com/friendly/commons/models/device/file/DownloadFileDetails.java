package com.friendly.commons.models.device.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadFileDetails {
    private Long id;
    private Integer priority;
    private Integer delay;
    private String fileType;
    private String fileName;
    private String username;
    private String password;
    private Integer fileSize;
    private String targetFileName;
    private DeliveryProtocolType deliveryProtocol;
    private DeliveryMethodType deliveryMethod;
    private String link;
    private String url;
    private String description;
    private Boolean isManual;

}
