package com.friendly.services.device.info.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeploymentUnitDetailsList {
    private List<DeploymentUnitDetails> items;
}
