package com.friendly.services.management.action.dto.request.inheritors.setattrib;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CpeParamAttribute {
    private String accessList;
    private String fullName;
    private String notification;
}
