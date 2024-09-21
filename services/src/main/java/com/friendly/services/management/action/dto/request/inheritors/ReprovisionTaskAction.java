package com.friendly.services.management.action.dto.request.inheritors;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
public class ReprovisionTaskAction extends AbstractActionRequest {
}
