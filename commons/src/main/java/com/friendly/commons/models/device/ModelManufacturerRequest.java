package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelManufacturerRequest {
    private String manufacturer;
    private String model;
}

