package com.friendly.services.management.action.dto.request.inheritors.changedustate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrUninstalOpDetailsForTask {
    private String version;
    private Long nameId;
    private String name;
}
