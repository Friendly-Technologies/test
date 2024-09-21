package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.ChangeDUStateTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ChangeDUStateTaskActionResponse;
import com.ftacs.ChangeDUSateTask;
import com.ftacs.InstallOpListWS;
import com.ftacs.UnInstallOpListWS;
import com.ftacs.UnInstallOpStructWS;
import com.ftacs.UpdateOpListWS;
import com.ftacs.UpdateTaskWS;

import java.util.List;

public class ActionChangeDUStateConverter implements ActionConverter<ChangeDUStateTaskAction, ChangeDUStateTaskActionResponse> {

    @Override
    public UpdateTaskWS convertToEntity(ChangeDUStateTaskAction request) {
        final ChangeDUSateTask changeDUState = new ChangeDUSateTask();
        final InstallOpListWS installOpListWS = new InstallOpListWS();

        changeDUState.setOrder(request.getOrder());
//
//        request.getInstallOperations().forEach(e -> {
//            InstallOpStructWS installOpStructWS = new InstallOpStructWS();
//            installOpStructWS.setPassword(e.getPassword());
//            installOpStructWS.setReprovision(true);
//            installOpStructWS.setUrl(e.getUrl());
//            installOpStructWS.setUsername(e.getUsername());
//            installOpStructWS.setUuid(e.getUuid());
//            installOpListWS.getInstallOperation().add(installOpStructWS);
//        });
        changeDUState.setInstallOperations(installOpListWS);

        final UnInstallOpListWS unInstallOpListWS = new UnInstallOpListWS();
        request.getUnInstallOperations().forEach(e -> {
            UnInstallOpStructWS unInstallOpStructWS = new UnInstallOpStructWS();
            unInstallOpStructWS.setUuid(e.getUuid());
//            unInstallOpWS.setNameId(newService.getIdByName(request.getName()).intValue());
//            unInstallOpStructWS.setVersion(e.getVersion());
            unInstallOpListWS.getUnInstallOperation().add(unInstallOpStructWS);
        });
        changeDUState.setUnInstallOperations(unInstallOpListWS);

        final UpdateOpListWS updateOpListWS = new UpdateOpListWS();
//        request.getUpdateOperations().forEach(e -> {
//            UpdateOpStructWS updateOpStructWS = new UpdateOpStructWS();
//            updateOpStructWS.setPassword(e.getPassword());
//            updateOpStructWS.setUrl(e.getUrl());
//            updateOpStructWS.setUuid(e.getUuid());
//            updateOpStructWS.setUsername(e.getUsername());
//            updateOpStructWS.setVersion(e.getVersion());
//            updateOpListWS.getUpdateOperation().add(updateOpStructWS);
//        });
        changeDUState.setUpdateOperations(updateOpListWS);
        return null;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
//        final ActionListResponse<ChangeDUStateTaskActionResponse> response = new ActionListResponse();
//        final List<InstallOpResponse> installOpResponses = new ArrayList<>();
//        final List<UninstallOpResponse> uninstallOpResponses = new ArrayList<>();
//        final List<UpdateOpResponse> updateOpResponses = new ArrayList<>();
//        ActionTypeEnum type = null;
//        if (!action.getActionOpInstallList().isEmpty()) {
//            action.getActionOpInstallList()
//                    .forEach(e -> installOpResponses.add(InstallOpResponse.builder()
//                            .url(e.getUrl())
//                            .username(e.getUsername())
//                            .password(e.getPassword())
//                            .uuid(e.getUuid())
//                            .build()));
//            type = ActionTypeEnum.INSTALL_TASK;
//        }
//        if (!action.getActionOpUninstallList().isEmpty()) {
//            action.getActionOpUninstallList()
//                    .forEach(e -> uninstallOpResponses.add(UninstallOpResponse.builder()
//                            .uuid(e.getUuid())
//                            .version(e.getVersion())
//                            .build()));
//            type = ActionTypeEnum.UNINSTALL_TASK;
//        }
//        if (!action.getActionOpUpdateList().isEmpty()) {
//            action.getActionOpUpdateList()
//                    .forEach(e -> updateOpResponses.add(UpdateOpResponse.builder()
//                            .password(e.getPassword())
//                            .url(e.getUrl())
//                            .username(e.getUsername())
//                            .build()));
//            type = ActionTypeEnum.UPDATE_SOFTWARE_TASK;
//        }
//        response.setOrder(action.getPriority());
//        response.setTaskType(type);
//        response.setParameters(Collections.singletonList(ActionParameters.<ChangeDUStateTaskActionResponse>builder()
//                .name("ChangeDUState")
//                .details(ChangeDUStateTaskActionResponse.builder()
//                        .installOperations(installOpResponses)
//                        .unInstallOperations(uninstallOpResponses)
//                        .updateOperations(updateOpResponses)
//                        .build()).build()));
        return null;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
//        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
//        final ChangeDUSateTask changeDUState = new ChangeDUSateTask();
//        final InstallOpListWS installOpListWS = new InstallOpListWS();
//
//        changeDUState.setOrder(action.getPriority());
//
//        action.getActionOpInstallList().forEach(e -> {
//            InstallOpStructWS installOpStructWS = new InstallOpStructWS();
//            installOpStructWS.setPassword(e.getPassword());
//            installOpStructWS.setUrl(e.getUrl());
//            installOpStructWS.setUsername(e.getUsername());
//            installOpStructWS.setNameId(e.getNameId());
//            installOpListWS.getInstallOperation().add(installOpStructWS);
//        });
//        changeDUState.setInstallOperations(installOpListWS);
//
//        final UnInstallOpListWS unInstallOpListWS = new UnInstallOpListWS();
//        action.getActionOpUninstallList().forEach(e -> {
//            UnInstallOpStructWS unInstallOpStructWS = new UnInstallOpStructWS();
//            unInstallOpStructWS.setUuid(e.getUuid());
//            unInstallOpStructWS.setNameId(e.getNameId());
//            unInstallOpStructWS.setVersion(e.getVersion());
//            unInstallOpListWS.getUnInstallOperation().add(unInstallOpStructWS);
//        });
//        changeDUState.setUnInstallOperations(unInstallOpListWS);
//
//        final UpdateOpListWS updateOpListWS = new UpdateOpListWS();
//        action.getActionOpUpdateList().forEach(e -> {
//            UpdateOpStructWS updateOpStructWS = new UpdateOpStructWS();
//            updateOpStructWS.setPassword(e.getPassword());
//            updateOpStructWS.setUrl(e.getUrl());
//            updateOpStructWS.setUuid(e.getUuid());
//            updateOpStructWS.setUsername(e.getUsername());
//            updateOpStructWS.setVersion(e.getVersion());
//            updateOpListWS.getUpdateOperation().add(updateOpStructWS);
//        });
//        changeDUState.setUpdateOperations(updateOpListWS);
//        updateTaskWSList.add(changeDUState);
        return null;
    }
}
