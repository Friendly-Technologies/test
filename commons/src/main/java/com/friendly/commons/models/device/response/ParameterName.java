package com.friendly.commons.models.device.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ParameterName {
    private Long nameId;
    private String shortName;
    private String type;

}
