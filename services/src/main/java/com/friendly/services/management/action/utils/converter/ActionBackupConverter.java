package com.friendly.services.management.action.utils.converter;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.BackupTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.BackupTaskActionResponse;
import com.friendly.services.settings.fileserver.FileServerService;
import com.ftacs.BackupCpeConfigTask;
import com.ftacs.CpeUploadFile;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ActionBackupConverter implements ActionConverter<BackupTaskAction, BackupTaskActionResponse> {
    private final FileServerService fileServerService;

    @Override
    public UpdateTaskWS convertToEntity(BackupTaskAction request) {
        final BackupCpeConfigTask backupCpeConfigTask = new BackupCpeConfigTask();
        final CpeUploadFile cpeUploadFile = new CpeUploadFile();
        final ServerDetails serverDetails = fileServerService.getServerDetails(ClientType.mc)   /*  ClientType.mc because we can use ug task`s functionality only on management center*/
                .stream()
                .filter(s -> s.getKey().equals("UploadHttp"))
                .findAny()
                .orElse(null);
        backupCpeConfigTask.setOrder(request.getOrder());
        cpeUploadFile.setFileTypeId(4);
        cpeUploadFile.setUrl(serverDetails.getAddress());
        cpeUploadFile.setReprovision(true);
        backupCpeConfigTask.setFile(cpeUploadFile);
        return backupCpeConfigTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.BACKUP_TASK);
        response.setOrder(action.getPriority());
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateGroupTaskWSList = new ArrayList<>();
        final BackupCpeConfigTask backupCpeConfigTask = new BackupCpeConfigTask();
        final CpeUploadFile cpeUploadFile = new CpeUploadFile();
        final ServerDetails serverDetails = fileServerService.getServerDetails(ClientType.mc)   /*  ClientType.mc because we can use ug task`s functionality only on management center*/
                .stream()
                .filter(s -> s.getKey().equals("UploadHttp"))
                .findAny()
                .orElse(null);
        backupCpeConfigTask.setOrder(action.getPriority());
        cpeUploadFile.setFileTypeId(4);
        cpeUploadFile.setUrl(serverDetails.getAddress());
        cpeUploadFile.setReprovision(true);
        backupCpeConfigTask.setFile(cpeUploadFile);
        updateGroupTaskWSList.add(backupCpeConfigTask);

        return updateGroupTaskWSList;
    }
}
