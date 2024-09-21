package com.friendly.commons.models.settings.security;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityUspMtp {
    private Integer id;
    private Integer securityId;
    private UnderlyingProtocolType mtpProtocolType;
    private SecurityModeType securityType;
}
