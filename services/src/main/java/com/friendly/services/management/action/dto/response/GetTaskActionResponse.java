package com.friendly.services.management.action.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTaskActionResponse extends AbstractActionResponse {
    private String fullName;
    private boolean names;
    private boolean values;
    private boolean attributes;
}
