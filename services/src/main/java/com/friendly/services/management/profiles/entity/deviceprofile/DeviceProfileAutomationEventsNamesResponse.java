package com.friendly.services.management.profiles.entity.deviceprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileAutomationEventsNamesResponse {
    private List<String> items;
}
