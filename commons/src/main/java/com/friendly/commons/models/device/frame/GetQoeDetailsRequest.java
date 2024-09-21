package com.friendly.commons.models.device.frame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetQoeDetailsRequest {
    private Long deviceId;
    private Long frameId;
    private ConditionFilter conditions;
}
