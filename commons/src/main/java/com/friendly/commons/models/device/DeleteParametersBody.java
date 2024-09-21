package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteParametersBody {
    private Integer deviceId;
    private ParameterNamesRequest request;
}
