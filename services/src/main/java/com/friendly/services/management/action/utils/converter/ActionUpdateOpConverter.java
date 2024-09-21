package com.friendly.services.management.action.utils.converter;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.device.info.service.DeviceSoftwareService;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.InstallOrUpdateTaskRequest;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.UpdateOpTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.UpdateOrUninstalOpDetailsForTask;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.UpdateOpResponse;
import com.friendly.services.settings.fileserver.FileServerService;
import com.ftacs.ChangeDUSateTask;
import com.ftacs.UpdateOpListWS;
import com.ftacs.UpdateOpStructWS;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ActionUpdateOpConverter  implements ActionConverter<UpdateOpTaskAction, UpdateOpResponse>{
    private final DeviceSoftwareService deviceSoftwareService;
    private final FileServerService fileServerService;
    @Setter
    private Long groupId;
    @Override
    public UpdateTaskWS convertToEntity(UpdateOpTaskAction request) {
        final ChangeDUSateTask changeDUState = new ChangeDUSateTask();
        final UpdateOpListWS updateOpListWS = new UpdateOpListWS();
        final InstallOrUpdateTaskRequest updateOpTaskRequest = request.getInstallOrUpdateTaskRequest();
        changeDUState.setOrder(request.getOrder());
        final String url = updateOpTaskRequest.getLink() == null
                && updateOpTaskRequest.getFileName() != null
                && !updateOpTaskRequest.getUrl().endsWith(updateOpTaskRequest.getFileName()) ?
                updateOpTaskRequest.getUrl().endsWith("/") ? updateOpTaskRequest.getUrl() + updateOpTaskRequest.getFileName()
                        : updateOpTaskRequest.getUrl() + "/" + updateOpTaskRequest.getFileName()
                : updateOpTaskRequest.getLink();
        final UpdateOrUninstalOpDetailsForTask updateOrUninstalOpDetailsForTask = deviceSoftwareService.getBranchFromTreeByUUID(groupId, request.getInstallOrUpdateTaskRequest().getUuid());
        final UpdateOpStructWS updateOpStructWS = new UpdateOpStructWS();
        updateOpStructWS.setPassword(updateOpTaskRequest.getPassword());
        updateOpStructWS.setUrl(url);
        updateOpStructWS.setUsername(updateOpTaskRequest.getUsername());
        updateOpStructWS.setUuid(updateOpTaskRequest.getUuid());
        updateOpStructWS.setVersion(updateOrUninstalOpDetailsForTask.getVersion());
        updateOpListWS.getUpdateOperation().add(updateOpStructWS);
        changeDUState.setUpdateOperations(updateOpListWS);
        return changeDUState;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        final List<ActionParameters> parameters = new ArrayList<>();
        final ServerDetails serverDetails = fileServerService.getServerDetails(ClientType.mc)   /*  ClientType.mc because we can use ug task`s functionality only on management center*/
                .stream()
                .filter(s -> s.getKey().equals("UploadHttp"))
                .findAny()
                .orElse(null);
        response.setTaskType(ActionTypeEnum.UPDATE_SOFTWARE_TASK);
        response.setOrder(action.getPriority());
        action.getActionOpUpdateList().forEach(e -> {
                    String link = e.getUrl();
                    String url = link == null || !link.contains("/") ? link : link.substring(0, link.lastIndexOf("/") + 1);
                    String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);
                    parameters.add(ActionParameters.<UpdateOpResponse>builder()
                            .name(e.getUrl())
                            .value(e.getUrl())
                            .details(UpdateOpResponse.builder()
                                    .url(url)
                                    .username(e.getUsername())
                                    .password(e.getPassword())
                                    .fileName(fileName)
                                    .link(link)
                                    .uuid(e.getUuid())
                                    .isManual(serverDetails != null && serverDetails.getAddress() != null
                                            && !e.getUrl().contains(serverDetails.getAddress()))
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
        final UpdateOpListWS updateOpListWS = new UpdateOpListWS();

        changeDUState.setOrder(action.getPriority());
        action.getActionOpUpdateList().forEach(e -> {
                    UpdateOpStructWS updateOpStructWS = new UpdateOpStructWS();
                    updateOpStructWS.setPassword(e.getPassword());
                    updateOpStructWS.setUrl(e.getUrl());
                    updateOpStructWS.setUsername(e.getUsername());
                    updateOpStructWS.setUuid(e.getUuid());
                    updateOpStructWS.setVersion(e.getVersion());
                    updateOpListWS.getUpdateOperation().add(updateOpStructWS);
                }
        );
        changeDUState.setUpdateOperations(updateOpListWS);
        updateTaskWSList.add(changeDUState);
        return updateTaskWSList;
    }
}
