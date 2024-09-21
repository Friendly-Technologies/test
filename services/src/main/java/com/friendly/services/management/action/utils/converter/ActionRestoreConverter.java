package com.friendly.services.management.action.utils.converter;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.RestoreTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.RestoreTaskActionResponse;
import com.friendly.services.settings.fileserver.FileServerService;
import com.ftacs.CpeFileWS;
import com.ftacs.RestoreCpeConfigTask;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ActionRestoreConverter implements ActionConverter<RestoreTaskAction, RestoreTaskActionResponse> {
    private final FileServerService fileServerService;
    @Override
    public UpdateTaskWS convertToEntity(RestoreTaskAction request) {
        final RestoreCpeConfigTask restoreCpeConfigTask = new RestoreCpeConfigTask();
        final CpeFileWS cpeFileWS = new CpeFileWS();
        final ServerDetails serverDetails = fileServerService.getServerDetails(ClientType.mc)   /*  ClientType.mc because we can use ug task`s functionality only on management center*/
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        restoreCpeConfigTask.setOrder(request.getOrder());
        cpeFileWS.setReprovision(true);
        cpeFileWS.setFileTypeId(3);
        cpeFileWS.setUrl(serverDetails.getAddress());
        restoreCpeConfigTask.setFile(cpeFileWS);
        return restoreCpeConfigTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.RESTORE_TASK);
        response.setOrder(action.getPriority());
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateGroupTaskWSList = new ArrayList<>();
        final RestoreCpeConfigTask restoreCpeConfigTask = new RestoreCpeConfigTask();
        final CpeFileWS cpeFileWS = new CpeFileWS();
        final ServerDetails serverDetails = fileServerService.getServerDetails(ClientType.mc)   /*  ClientType.mc because we can use ug task`s functionality only on management center*/
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        restoreCpeConfigTask.setOrder(action.getPriority());
        cpeFileWS.setReprovision(true);
        cpeFileWS.setFileTypeId(3);
        cpeFileWS.setUrl(serverDetails.getAddress());
        restoreCpeConfigTask.setFile(cpeFileWS);
        updateGroupTaskWSList.add(restoreCpeConfigTask);
        return updateGroupTaskWSList;
    }
}
