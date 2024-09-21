package com.friendly.services.management.action.utils.converter;

import com.friendly.services.device.info.service.DeviceSoftwareService;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.UninstallOpTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.UpdateOrUninstalOpDetailsForTask;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.UninstallOpResponse;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.ftacs.ChangeDUSateTask;
import com.ftacs.UnInstallOpListWS;
import com.ftacs.UnInstallOpStructWS;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ActionUninstallOpConverter implements ActionConverter<UninstallOpTaskAction, UninstallOpResponse> {
    private final DeviceSoftwareService deviceSoftwareService;
    @Setter
    private Long groupId;

    @Override
    public UpdateTaskWS convertToEntity(UninstallOpTaskAction request) {
        final ChangeDUSateTask changeDUState = new ChangeDUSateTask();
        final UnInstallOpListWS unInstallOpListWS = new UnInstallOpListWS();
        final UpdateOrUninstalOpDetailsForTask updateOrUninstalOpDetailsForTask = deviceSoftwareService.getBranchFromTreeByUUID(groupId, request.getUuid());
        changeDUState.setOrder(request.getOrder());
        final UnInstallOpStructWS unInstallOpWS = new UnInstallOpStructWS();
        unInstallOpWS.setUuid(request.getUuid());
        unInstallOpWS.setVersion(updateOrUninstalOpDetailsForTask.getVersion());
        unInstallOpWS.setNameId(CommonUtils.ACS_OBJECT_FACTORY.createUnInstallOpStructWSNameId(updateOrUninstalOpDetailsForTask.getNameId().intValue()));
        unInstallOpListWS.getUnInstallOperation().add(unInstallOpWS);
        changeDUState.setUnInstallOperations(unInstallOpListWS);
        return changeDUState;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        final List<ActionParameters> parameters = new ArrayList<>();
        response.setTaskType(ActionTypeEnum.UNINSTALL_TASK);
        response.setOrder(action.getPriority());
        action.getActionOpUninstallList().forEach(e -> {
                    UpdateOrUninstalOpDetailsForTask updateOrUninstalOpDetailsForTask = deviceSoftwareService.getBranchFromTreeByUUID(groupId, e.getUuid());
                    parameters.add(ActionParameters.<UninstallOpResponse>builder()
                            .name(updateOrUninstalOpDetailsForTask.getName() + " v. " +  e.getVersion())
                            .details(UninstallOpResponse.builder()
                                    .name(updateOrUninstalOpDetailsForTask.getName())
                                    .version(e.getVersion())
                                    .uuid(e.getUuid())
                                    .build())
                            .build());
                }
        );
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final ChangeDUSateTask changeDUState = new ChangeDUSateTask();
        final UnInstallOpListWS unInstallOpListWS = new UnInstallOpListWS();

        changeDUState.setOrder(action.getPriority());
        action.getActionOpUninstallList().forEach(e -> {
            UnInstallOpStructWS unInstallOpWS = new UnInstallOpStructWS();
            unInstallOpWS.setUuid(e.getUuid());
            unInstallOpWS.setVersion(e.getVersion());
            unInstallOpWS.setNameId(CommonUtils.ACS_OBJECT_FACTORY.createUnInstallOpStructWSNameId(e.getNameId()));
            unInstallOpListWS.getUnInstallOperation().add(unInstallOpWS);
        });
        changeDUState.setUnInstallOperations(unInstallOpListWS);
        updateTaskWSList.add(changeDUState);
        return updateTaskWSList;
    }
}
