package com.friendly.commons.models.device.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetParametersFullNames {
    private List<String> items;
}
