package com.friendly.commons.models.settings.bootstrap;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Device Activity
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapSecurity implements Serializable {

    private Integer id;
    private Integer bootstrapId;
    private Integer securityId;
    private Integer instanceId;
    private Integer serverId;
    private Integer osInstanceId;
    private Integer holdOffTime;
    private String serverUri;
    private Boolean isBootstrap;
    private SecurityModeType securityType;
}
