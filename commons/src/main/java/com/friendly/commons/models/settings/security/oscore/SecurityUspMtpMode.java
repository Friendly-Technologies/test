package com.friendly.commons.models.settings.security.oscore;

import com.friendly.commons.models.settings.security.UnderlyingProtocolType;
import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspMtpMode {
    private UnderlyingProtocolType type;
    private List<SecurityModeType> modes;
}
