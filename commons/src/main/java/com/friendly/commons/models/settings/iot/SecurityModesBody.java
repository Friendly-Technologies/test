package com.friendly.commons.models.settings.iot;

import com.friendly.commons.models.settings.security.ProtocolSecurityType;
import com.friendly.commons.models.settings.security.UnderlyingProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of IOT controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SecurityModesBody implements Serializable {
	private ProtocolSecurityType protocolType;
    private UnderlyingProtocolType underlyingProtocol;
    
}
