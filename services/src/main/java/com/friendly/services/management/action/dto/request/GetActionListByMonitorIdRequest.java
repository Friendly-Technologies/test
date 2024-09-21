package com.friendly.services.management.action.dto.request;

import com.friendly.services.management.action.dto.enums.ActionOwnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetActionListByMonitorIdRequest {
    private String manufacturer;
    private String model;
    private Integer id;
    private ActionOwnerTypeEnum type;
}