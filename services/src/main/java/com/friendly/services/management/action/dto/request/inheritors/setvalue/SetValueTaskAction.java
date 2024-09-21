package com.friendly.services.management.action.dto.request.inheritors.setvalue;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetValueTaskAction extends AbstractActionRequest {
    private List<CpeParam> cpeParamList;
}
