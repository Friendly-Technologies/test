package com.friendly.commons.models.view;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Data
@Builder
public class ConditionItem {
    private Long viewId;
    private Integer viewIndex;
    private String viewName;
    
    List<ViewCondition> conditions;
}
