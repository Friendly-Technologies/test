package com.friendly.commons.models.settings.bootstrap;

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
public class BootstrapServer implements Serializable {

    private Integer id;
    private Integer bootstrapId;
    private Integer instanceId;
    private Integer serverId;

    private String serverUri;
    private Integer lifeTime;
    private Integer minPeriod;
    private Integer maxPeriod;
    private Integer disableTimeout;
    private Boolean notification;
    private String binding;

}
