package com.friendly.services.management.profiles.entity.deviceprofile;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonSubTypes({
        @JsonSubTypes.Type(DeviceProfileDetailRequest.class),
        @JsonSubTypes.Type(DeviceProfileDetailResponse.class)
})
public class DeviceProfileDetail {
    private Integer id;
    private String manufacturer;
    private String model;
    private String name;
    private Boolean dataTree;
    private Boolean reprovision;
    private Long conditionId;
    private Boolean sendBackup;
    private Boolean sendBackupForNewDevicesOnly;
    private List<DeviceProfileParameter> parameters;
    private List<DeviceProfileNotificationAccess> policy;
}
