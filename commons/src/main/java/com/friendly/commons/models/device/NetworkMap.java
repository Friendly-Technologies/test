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
public class NetworkMap {
    private Instant created;
    private String name;
    private String root;
    private String active;
    private String interfaceType;
    private String layer1Interface;
    private String layer3Interface;
    private String hostname;
    private String mac;
}
