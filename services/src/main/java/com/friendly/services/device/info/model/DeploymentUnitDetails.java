package com.friendly.services.device.info.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeploymentUnitDetails {
    private String deploymentUnitName;
    private String version;
    private String status;
    private String description;
    private String uuid;
}
