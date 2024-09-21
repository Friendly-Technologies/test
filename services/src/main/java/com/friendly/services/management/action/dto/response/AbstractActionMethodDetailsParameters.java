package com.friendly.services.management.action.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractActionMethodDetailsParameters extends AbstractActionResponse {
    private String method;

}
