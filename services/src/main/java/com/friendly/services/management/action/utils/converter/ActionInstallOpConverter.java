package com.friendly.services.management.action.utils.converter;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.InstallOpTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.InstallOrUpdateTaskRequest;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.InstallOpResponse;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.ftacs.ChangeDUSateTask;
import com.ftacs.InstallOpListWS;
import com.ftacs.InstallOpStructWS;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class ActionInstallOpConverter  implements ActionConverter<InstallOpTaskAction, InstallOpResponse>{
    private final FileServerService fileServerService;

    @Override
    public UpdateTaskWS convertToEntity(InstallOpTaskAction request) {
        final ChangeDUSateTask changeDUState = new ChangeDUSateTask();
        final InstallOpListWS installOpListWS = new InstallOpListWS();
        final InstallOrUpdateTaskRequest installOpTaskRequest = request.getInstallOrUpdateTaskRequest();
        final String url = installOpTaskRequest.getLink() == null
                && installOpTaskRequest.getFileName() != null
                && !installOpTaskRequest.getUrl().endsWith(installOpTaskRequest.getFileName()) ?
                installOpTaskRequest.getUrl().endsWith("/") ? installOpTaskRequest.getUrl() + installOpTaskRequest.getFileName()
                        : installOpTaskRequest.getUrl() + "/" + installOpTaskRequest.getFileName()
                : installOpTaskRequest.getLink();
        changeDUState.setOrder(request.getOrder());
        final InstallOpStructWS installOpStructWS = new InstallOpStructWS();
            installOpStructWS.setPassword(installOpTaskRequest.getPassword());
            installOpStructWS.setReprovision(true);
            installOpStructWS.setUrl(url);
            installOpStructWS.setUsername(installOpTaskRequest.getUsername());
            installOpStructWS.setUuid(CommonUtils.ACS_OBJECT_FACTORY.createInstallOpStructWSUuid(installOpTaskRequest.getUuid()));
            installOpListWS.getInstallOperation().add(installOpStructWS);
        changeDUState.setInstallOperations(installOpListWS);
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
        response.setTaskType(ActionTypeEnum.INSTALL_TASK);
        response.setOrder(action.getPriority());
        action.getActionOpInstallList().forEach(e -> {
                    String link = e.getUrl();
                    String url = link == null || !link.contains("/") ? link : link.substring(0, link.lastIndexOf("/") + 1);
                    String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);
                    parameters.add(ActionParameters.<InstallOpResponse>builder()
                            .value(e.getUrl())
                            .details(InstallOpResponse.builder()
                                    .url(url)
                                    .password(e.getPassword())
                                    .username(e.getUsername())
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
        final InstallOpListWS installOpListWS = new InstallOpListWS();

        changeDUState.setOrder(action.getPriority());

        action.getActionOpInstallList().forEach(e -> {
            InstallOpStructWS installOpStructWS = new InstallOpStructWS();
            installOpStructWS.setPassword(e.getPassword());
            installOpStructWS.setUrl(e.getUrl());
            installOpStructWS.setUsername(e.getUsername());
            installOpStructWS.setNameId(e.getNameId());
            installOpListWS.getInstallOperation().add(installOpStructWS);
        });
        changeDUState.setInstallOperations(installOpListWS);
        updateTaskWSList.add(changeDUState);
        return updateTaskWSList;
    }
}
