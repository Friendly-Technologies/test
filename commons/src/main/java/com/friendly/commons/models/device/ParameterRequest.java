package com.friendly.commons.models.device;

import lombok.Data;

import java.util.List;

@Data
public class ParameterRequest {
    List<InvokeParameter> parameters;
}
