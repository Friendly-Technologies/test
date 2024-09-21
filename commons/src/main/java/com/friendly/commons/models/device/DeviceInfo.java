package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that represents API version of Device
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo implements Serializable {

    private Long id;
    private String created;
    private Instant createdIso;
    private String updated;
    private Instant updatedIso;
    private DeviceStatusType status;
    private String serial;
    private String manufacturer;
    private String model;
    private String oui;
    private String firmware;
    private String hardware;
    private String ipAddress;
    private String macAddress;
    private String uptime;
    private ProtocolType protocolType;
    private String protocolVersion;
    private String batteryLevel;

}
