package com.friendly.commons.models.device.provision;

import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.DeliveryProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadProvisionDetails {
    private Long id;
    private Integer priority;
    private Integer delay;
    private String fileType;
    private String fileName;
    private String username;
    private String password;
    private Integer fileSize;
//    private FileLocation fileLocation;
    private DeliveryProtocolType deliveryProtocol;
    private DeliveryMethodType deliveryMethod;
    private String link;
    private String url;
    private String description;
    private Boolean isManual;
    private String targetFileName;
}
