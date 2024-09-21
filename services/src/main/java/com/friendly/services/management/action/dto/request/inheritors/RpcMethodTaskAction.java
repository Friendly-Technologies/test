package com.friendly.services.management.action.dto.request.inheritors;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcMethodTaskAction extends AbstractActionRequest {
    private String method;
    private boolean reprovision;
    private String request;
}
