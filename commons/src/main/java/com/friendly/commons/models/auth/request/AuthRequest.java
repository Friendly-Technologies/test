package com.friendly.commons.models.auth.request;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.auth.NotificationType;
import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String resetPasswordKey;
    private Integer timeZoneOffsetMin;
    private ClientType clientType;
    private String clientVersion;
    private NotificationType notificationType;
}
