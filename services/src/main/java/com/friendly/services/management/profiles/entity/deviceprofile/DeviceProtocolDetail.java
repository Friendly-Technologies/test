package com.friendly.services.management.profiles.entity.deviceprofile;

import com.friendly.commons.models.device.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProtocolDetail implements Serializable {
    private ProtocolType protocolType;
    private String protocolVersion;
}
