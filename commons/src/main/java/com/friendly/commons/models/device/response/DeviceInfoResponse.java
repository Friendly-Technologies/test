package com.friendly.commons.models.device.response;

import com.friendly.commons.models.device.DeviceInfo;
import com.friendly.commons.models.device.DeviceStatusType;
import com.friendly.commons.models.device.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoResponse {
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


    public static DeviceInfoResponse fromDeviceInfo(DeviceInfo deviceInfo) {
        return DeviceInfoResponse.builder()
                .id(deviceInfo.getId())
                .created(deviceInfo.getCreated())
                .createdIso(deviceInfo.getCreatedIso())
                .updated(deviceInfo.getUpdated())
                .updatedIso(deviceInfo.getUpdatedIso())
                .status(deviceInfo.getStatus())
                .serial(deviceInfo.getSerial())
                .manufacturer(deviceInfo.getManufacturer())
                .model(deviceInfo.getModel())
                .oui(deviceInfo.getOui())
                .firmware(deviceInfo.getFirmware())
                .hardware(deviceInfo.getHardware())
                .ipAddress(deviceInfo.getIpAddress())
                .macAddress(deviceInfo.getMacAddress())
                .uptime(deviceInfo.getUptime())
                .protocolType(deviceInfo.getProtocolType())
                .protocolVersion(deviceInfo.getProtocolVersion())
                .batteryLevel(deviceInfo.getBatteryLevel())
                .build();
    }
}
