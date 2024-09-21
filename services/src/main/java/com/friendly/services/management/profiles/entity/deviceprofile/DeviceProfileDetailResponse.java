package com.friendly.services.management.profiles.entity.deviceprofile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileDetailResponse extends DeviceProfileDetail {
    private Instant createdIso;
    private String created;
    private String version;
    private List<DeviceProfileDownloadFileDetailResponse> files;
    private List<DeviceProfileAutomationEventsResponse> automationEvents;
    private List<DeviceProfileAutomationParameterResponse> automationParameters;
}
