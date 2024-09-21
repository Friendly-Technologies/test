package com.friendly.services.management.action.dto.response;

import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActionListResponse {
    private ActionTypeEnum taskType;
    private Integer order;
    private List<ActionParameters> parameters;
}
