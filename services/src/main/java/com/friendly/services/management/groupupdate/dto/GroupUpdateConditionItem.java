package com.friendly.services.management.groupupdate.dto;

import com.friendly.commons.models.view.ViewCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateConditionItem {
    private Long id;
    private String name;
    private List<ViewCondition> conditions;
}
