package com.friendly.services.management.action.dto.request.inheritors;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallApiTaskAction extends AbstractActionRequest {
    private String apiUrl;
    private String apiRequest;
    private String apiMethodName;
}
