package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserExperienceBody {
    private Long deviceId;
    private UserExperienceConditionFilter conditions;
}
