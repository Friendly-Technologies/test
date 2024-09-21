package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class DeviceHistoryDetails {
    private String parameterName;
    private String oldValue;
    private String value;
    private String created;
    private Instant createdIso;
}
