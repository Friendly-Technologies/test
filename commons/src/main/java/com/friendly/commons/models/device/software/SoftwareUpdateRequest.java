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
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class SoftwareUpdateRequest implements Serializable {
    private String username;
    private String password;
    private String url;
    private String fileName;
    private String link;
    private String version;
    private String uuid;
    private Boolean resetSession;
    private Boolean push;
    private Integer priority;
    private Long deviceId;
}
