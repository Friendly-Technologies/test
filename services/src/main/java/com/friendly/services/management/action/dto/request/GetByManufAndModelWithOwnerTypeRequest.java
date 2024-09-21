package com.friendly.services.management.action.dto.request;

import com.friendly.services.management.action.dto.enums.ActionOwnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetByManufAndModelWithOwnerTypeRequest {
    private String manufacturer;
    private String model;
    private ActionOwnerTypeEnum type;
}
