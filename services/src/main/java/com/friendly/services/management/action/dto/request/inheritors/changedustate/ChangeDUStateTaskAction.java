package com.friendly.services.management.action.dto.request.inheritors.changedustate;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDUStateTaskAction extends AbstractActionRequest {
    protected List<InstallOpTaskAction> installOperations;
    protected List<UpdateOpTaskAction> updateOperations;
    protected List<UninstallOpTaskAction> unInstallOperations;
}
