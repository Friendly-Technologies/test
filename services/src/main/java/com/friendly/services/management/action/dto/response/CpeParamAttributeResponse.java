package com.friendly.services.management.action.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CpeParamAttributeResponse {
    private Integer order;
    private String accessList;
    private String fullName;
    private String notification;
}
