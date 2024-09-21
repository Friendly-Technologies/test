package com.friendly.commons.models.device.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetNewParams {
    List<ParameterName> items;
}
