package com.friendly.services.management.action.utils.converter;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.file.FileActType;
import com.friendly.commons.models.device.file.FileUploadRequest;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.filemanagement.orm.acs.repository.FileTypeRepository;
import com.friendly.services.management.profiles.orm.acs.repository.DeviceProfileRepository;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.UploadTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.UploadTaskActionResponse;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.ftacs.CpeUploadFile;
import com.ftacs.UpdateTaskWS;
import com.ftacs.UploadTask;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ActionUploadConverter implements ActionConverter<UploadTaskAction, UploadTaskActionResponse> {
    private final FileTypeRepository fileTypeRepository;
    private final FileServerService fileServerService;
    private final TemplateService templateService;
    private final DeviceProfileRepository deviceProfileRepository;

    @Override
    public UpdateTaskWS convertToEntity(UploadTaskAction request) {
        final UploadTask uploadTask = new UploadTask();
        final CpeUploadFile cpeUploadFile = new CpeUploadFile();
        final FileUploadRequest fileUploadRequest = request.getFileRequests();
        uploadTask.setOrder(request.getOrder());
        cpeUploadFile.setFileName(fileUploadRequest.getDescription());
        cpeUploadFile.setFileTypeId(fileUploadRequest.getFileTypeId());
        cpeUploadFile.setDelaySeconds(fileUploadRequest.getDelay());
        cpeUploadFile.setInstance(CommonUtils.ACS_OBJECT_FACTORY.createCpeUploadFileInstance(fileUploadRequest.getInstance()));
        cpeUploadFile.setPassword(fileUploadRequest.getPassword());
        final String url = fileUploadRequest.getIsManual() == null || fileUploadRequest.getIsManual() == Boolean.FALSE
                ? buildUrl(fileUploadRequest.getFileName(), fileUploadRequest.getUrl(), fileUploadRequest.getFileTypeId()) :
                fileUploadRequest.getLink();
        cpeUploadFile.setUrl(url);
        cpeUploadFile.setReprovision(fileUploadRequest.getReprovision());
        cpeUploadFile.setUsername(fileUploadRequest.getUsername());
        uploadTask.setFile(cpeUploadFile);
        return uploadTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        final List<ActionParameters> parameters = new ArrayList<>();
        response.setTaskType(ActionTypeEnum.UPLOAD_TASK);
        final ServerDetails serverDetails = fileServerService.getServerDetails(ClientType.mc)   /*  ClientType.mc because we can use ug task`s functionality only on management center*/
                .stream()
                .filter(s -> s.getKey().equals("UploadHttp"))
                .findAny()
                .orElse(null);
        action.getActionUploadList().forEach(e -> {
            String link = e.getUrl();
            String url = link == null || !link.contains("/") ? link : link.substring(0, link.lastIndexOf("/") + 1);
            String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);
                    parameters.add(ActionParameters.<UploadTaskActionResponse>builder()
                            .name(fileTypeRepository.findNameById(e.getFileTypeId()))
                            .value(e.getUrl())
                            .details(UploadTaskActionResponse.builder()
                                    .username(e.getUsername())
                                    .url(url)
                                    .fileName(fileName)
                                    .password(e.getPassword())
                                    .description(e.getFilename())
                                    .instance(getInstancesByManufacturerAndModelAndFileType(
                                            deviceProfileRepository.getProfileByAutomationId(action.getUgId(), action.getOwnerType()).getGroupId(),
                                            e.getFileTypeId()))
                                    .fileTypeId(e.getFileTypeId())
                                    .delay(e.getDelaySeconds())
                                    .link(link)
                                    .actType(FileActType.UPLOAD)
                                    .isManual(serverDetails != null && serverDetails.getAddress() != null
                                            && !e.getUrl().contains(serverDetails.getAddress()))
                                    .build())
                            .build());
                }
        );
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();


        action.getActionUploadList().forEach(e -> {
            UploadTask uploadTask = new UploadTask();
            CpeUploadFile cpeUploadFile = new CpeUploadFile();
            uploadTask.setOrder(action.getPriority());
            cpeUploadFile.setFileName(e.getFilename());
            cpeUploadFile.setFileTypeId(e.getFileTypeId());
            cpeUploadFile.setDelaySeconds(e.getDelaySeconds());
            cpeUploadFile.setInstance(CommonUtils.ACS_OBJECT_FACTORY.createCpeUploadFileInstance(e.getInstance()));
            cpeUploadFile.setPassword(e.getPassword());
            cpeUploadFile.setUrl(e.getUrl());
            cpeUploadFile.setUsername(e.getUsername());
            uploadTask.setFile(cpeUploadFile);
            updateTaskWSList.add(uploadTask);
        });

        return updateTaskWSList;
    }
    private String buildUrl(final String fileName, String url, final Integer fileType) {
        url = url.endsWith("/") ? url : url + "/";
        switch (fileType) {
            case 4:
            case 6:
            case 13:
                url += fileName + ".cfg";
                break;
            case 5:
            case 7:
            case 14:
                url += fileName + ".log";
                break;
            default:
        }
        return url;
    }

    private Integer getInstancesByManufacturerAndModelAndFileType(final Long groupId, final Integer fileTypeId){
        if (fileTypeId == 6) {
            return templateService.getParamNamesLike(groupId, "%DeviceInfo.VendorConfigFile.%.")
                    .stream()
                    .map(s -> org.apache.commons.lang3.StringUtils.substringBetween(s, "VendorConfigFile.", "."))
                    .map(Integer::parseInt)
                    .findFirst().orElse(null);
        }
        if (fileTypeId == 7) {
            return templateService.getParamNamesLike(groupId, "%DeviceInfo.VendorLogFile.%.")
                    .stream()
                    .map(s -> org.apache.commons.lang3.StringUtils.substringBetween(s, "VendorLogFile.", "."))
                    .map(Integer::parseInt)
                    .findFirst().orElse(null);
        }
        return null;
    }
}
