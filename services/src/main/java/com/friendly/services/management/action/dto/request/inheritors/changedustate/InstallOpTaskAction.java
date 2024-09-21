package com.friendly.services.management.action.dto.request.inheritors.changedustate;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstallOpTaskAction extends AbstractActionRequest {
    private InstallOrUpdateTaskRequest installOrUpdateTaskRequest;
}
