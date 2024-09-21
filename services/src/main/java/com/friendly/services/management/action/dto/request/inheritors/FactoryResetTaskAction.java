package com.friendly.services.management.action.dto.request.inheritors;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FactoryResetTaskAction extends AbstractActionRequest {
}
