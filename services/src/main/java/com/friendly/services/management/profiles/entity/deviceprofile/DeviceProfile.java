package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.services.management.profiles.entity.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfile {
    private Integer id;
    private ProfileStatus status;
    private String domainName;
    private String manufacturer;
    private String model;
    private String name;
    private String version;
    private String created;
    private Instant createdIso;
    private String application;
    private String creator;

}
