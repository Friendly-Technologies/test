package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.security.oscore.SecurityUspMtpMode;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspMtpModeResponse {
   private List<SecurityUspMtpMode> items;
}
