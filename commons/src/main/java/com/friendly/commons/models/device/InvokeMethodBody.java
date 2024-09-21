package com.friendly.commons.models.device;

import lombok.Getter;

@Getter
public class InvokeMethodBody {
    private Long deviceId;
    private String methodName;
    private ParameterRequest request;
}
