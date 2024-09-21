package com.friendly.commons.models.device.provision;

import com.friendly.commons.models.device.ProvisionParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
public class ProvisionObjectRequest extends AbstractProvisionRequest {
    private List<ProvisionParam> parameters;
}
