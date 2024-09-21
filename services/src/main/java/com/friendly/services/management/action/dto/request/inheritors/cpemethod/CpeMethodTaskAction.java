package com.friendly.services.management.action.dto.request.inheritors.cpemethod;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CpeMethodTaskAction extends AbstractActionRequest {
    private CpeMethodParameter cpeMethod;
}
