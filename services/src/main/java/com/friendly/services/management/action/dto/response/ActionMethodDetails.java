package com.friendly.services.management.action.dto.response;

import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ActionMethodDetails {
    ActionTypeEnum type;
    String description;
    List<AbstractActionMethodDetailsParameters> details;
}
