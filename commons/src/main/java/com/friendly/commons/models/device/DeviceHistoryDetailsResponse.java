package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DeviceHistoryDetailsResponse {
    private List<DeviceHistoryDetails> items;
}
