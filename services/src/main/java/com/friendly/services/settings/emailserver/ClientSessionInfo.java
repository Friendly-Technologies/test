package com.friendly.services.settings.emailserver;

import com.friendly.commons.models.auth.ClientType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientSessionInfo {
    private ClientType clientType;
    private Long userId;
}
