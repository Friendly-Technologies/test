package com.friendly.commons.models.settings.iot.request;

import com.friendly.commons.models.settings.security.ProtocolSecurityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolSecurityTypeRequest {
    ProtocolSecurityType protocolType;
}
