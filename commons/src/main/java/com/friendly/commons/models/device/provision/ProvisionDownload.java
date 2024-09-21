package com.friendly.commons.models.device.provision;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ProvisionDownload extends AbstractProvision {

    private String fileType;
    private String description;
    private Boolean isManual = false;
    private String link;
    private String fileName;
    private String url;
}
