package com.friendly.services.management.action.dto.request.inheritors.getparam;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTaskAction extends AbstractActionRequest {
    private List<GetEntry> parameters;
}
