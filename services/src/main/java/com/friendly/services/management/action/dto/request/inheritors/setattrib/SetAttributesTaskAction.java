package com.friendly.services.management.action.dto.request.inheritors.setattrib;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetAttributesTaskAction extends AbstractActionRequest {
    private List<CpeParamAttribute> cpeParamAttributeList;
}
