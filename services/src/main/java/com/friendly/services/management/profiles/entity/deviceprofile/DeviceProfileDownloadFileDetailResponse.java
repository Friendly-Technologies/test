package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.DeliveryProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileDownloadFileDetailResponse {
    private Integer delay;
    private Integer fileTypeId;
    private String fileName;
    private String username;
    private String password;
    private Integer fileSize;
    private String targetFileName;
    private DeliveryProtocolType deliveryProtocol;
    private DeliveryMethodType deliveryMethod;
    private String link;
    private String url;
    private Boolean isManual;
    private Instant createdIso;
    private String creator;
    private String created;
}
