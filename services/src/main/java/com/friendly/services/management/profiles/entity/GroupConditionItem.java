package com.friendly.services.management.profiles.entity;


import com.friendly.commons.models.view.ViewCondition;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupConditionItem {
    private Long id;
    private String name;
    private String manufacturer;
    private String model;
    private List<GroupConditionFilterItem> conditions;
}
