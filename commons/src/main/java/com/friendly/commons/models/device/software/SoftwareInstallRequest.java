package com.friendly.commons.models.device.software;

import com.friendly.commons.models.device.provision.AbstractProvision;
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
public class SoftwareInstallRequest implements Serializable {
    private String username;
    private String password;
    private String url;
    private String fileName;
    private String link;
//    private String fullName;
    private Boolean reprovision;
    private String uuid;
    private Boolean resetSession;
    private Boolean push;
    private Integer priority;
    private Long deviceId;
}
