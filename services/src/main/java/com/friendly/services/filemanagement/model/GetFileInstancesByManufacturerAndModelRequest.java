package com.friendly.services.filemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetFileInstancesByManufacturerAndModelRequest {
    private String manufacturer;
    private String model;
    private Integer fileTypeId;
}
