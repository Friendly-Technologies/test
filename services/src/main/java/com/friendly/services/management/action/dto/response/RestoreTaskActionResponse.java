package com.friendly.services.management.action.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestoreTaskActionResponse extends AbstractActionResponse {
    private String name;
}
