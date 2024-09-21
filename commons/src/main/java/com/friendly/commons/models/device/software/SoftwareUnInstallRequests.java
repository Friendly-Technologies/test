package com.friendly.commons.models.device.software;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareUnInstallRequests implements Serializable {
    private Boolean push;
    private Long deviceId;
    private List<SoftwareUnInstallRequest> items;
}
