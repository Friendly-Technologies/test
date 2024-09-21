package com.friendly.commons.models.device.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AddObjectRequest {
    private Boolean push;
    private Boolean reprovision;
    private String objectName;

    private List<NewObjectParam> parameters;
}
