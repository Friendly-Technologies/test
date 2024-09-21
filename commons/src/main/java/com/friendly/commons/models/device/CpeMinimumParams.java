package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CpeMinimumParams {
    private Long cpeId;
    private String paramName;
    private String paramValue;
}
