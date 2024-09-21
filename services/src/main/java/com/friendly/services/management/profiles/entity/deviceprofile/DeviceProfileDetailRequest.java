package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.commons.models.device.file.FileDownloadRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileDetailRequest extends DeviceProfileDetail {
    private List<FileDownloadRequest> files;
    private List<DeviceProfileAutomationEventsRequest> automationEvents;
    private List<DeviceProfileAutomationParametersRequest> automationParameters;
}
