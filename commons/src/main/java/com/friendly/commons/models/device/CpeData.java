package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CpeData {
    private Instant created;
    private String serial;
    private Integer nameId;
    private String name;
    private String mac;
    private String rssi;
    private String signal;
}
