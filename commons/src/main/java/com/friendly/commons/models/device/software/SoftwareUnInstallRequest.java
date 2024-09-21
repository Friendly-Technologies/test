package com.friendly.commons.models.device.software;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Model that represents API version of Provision
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SoftwareUnInstallRequest implements Serializable {
    private String version;
//    private String fullName;
    private String uuid;
    private Boolean resetSession;
    private Integer priority;
}
