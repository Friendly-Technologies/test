package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParameterNamesRequest {
    private List<String> parameterNames;
}

