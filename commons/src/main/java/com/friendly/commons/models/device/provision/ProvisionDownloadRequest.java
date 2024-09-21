package com.friendly.commons.models.device.provision;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Model that represents API version of Provision
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class ProvisionDownloadRequest extends AbstractProvisionRequest {
    private String name;
    private String fileName;
    private Integer delay;
    private Long fileTypeId;
    private Integer fileSize;
    private String username;
    private String password;
    private String link;
    private String url;
    private String description;
    private String targetFileName;
}
