package com.friendly.commons.models.device;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserExperienceConditionFilter {
    private UserExperienceConditionType compare;
    private Instant conditionDateIso;
    private Instant conditionFromDateIso;
    private Instant conditionToDateIso;
    private String conditionString;
}
