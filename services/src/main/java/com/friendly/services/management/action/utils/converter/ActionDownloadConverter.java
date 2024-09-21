package com.friendly.services.management.action.utils.converter;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.FileActType;
import com.friendly.commons.models.device.file.FileDownloadRequest;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.filemanagement.orm.acs.repository.FileTypeRepository;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.DownloadTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.DownloadTaskActionResponse;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.ftacs.CpeFileWS;
import com.ftacs.DownloadTask;
import com.ftacs.UpdateTaskWS;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ActionDownloadConverter implements ActionConverter<DownloadTaskAction, DownloadTaskActionResponse> {
    private final DeviceMapper deviceMapper;
    private final FileTypeRepository fileTypeRepository;
    private final FileServerService fileServerService;


    @Override
    public UpdateTaskWS convertToEntity(DownloadTaskAction request) {
        final DownloadTask downloadTask = new DownloadTask();
        final CpeFileWS cpeFileWS = new CpeFileWS();
        downloadTask.setOrder(request.getOrder());

        FileDownloadRequest fileRequest = request.getFileDownloadRequests();
        cpeFileWS.setDelaySeconds(fileRequest.getDelay());
        cpeFileWS.setFileContent(fileRequest.getFileContent());
        cpeFileWS.setFileName(fileRequest.getDescription());
        cpeFileWS.setFileSize(fileRequest.getFileSize());
        cpeFileWS.setFileTypeId(fileRequest.getFileTypeId());
        cpeFileWS.setFileVersion(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSFileVersion(fileRequest.getFileVersion()));
        cpeFileWS.setDeliveryMethod(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryMethod(deviceMapper.deliveryMethodToInteger(
                fileRequest.getDeliveryMethod() != null
                        ? fileRequest.getDeliveryMethod() : DeliveryMethodType.NotSet)));
        cpeFileWS.setNewest(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSNewest(fileRequest.getNewest()));
        cpeFileWS.setPassword(fileRequest.getPassword());
        cpeFileWS.setReset(fileRequest.getResetSession());
        cpeFileWS.setReprovision(fileRequest.getReprovision());
        cpeFileWS.setDeliveryProtocol(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryProtocol(fileRequest.getDeliveryProtocol() != null
                ? deviceMapper.deliveryProtocolToInteger(fileRequest.getDeliveryProtocol()) : -1)); // -1 -> DeliveryProtocol.NotSet
        cpeFileWS.setFailureURL(fileRequest.getFailureURL());
        cpeFileWS.setSendBytes(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSSendBytes(fileRequest.getSendBytes()));
        cpeFileWS.setSuccessURL(fileRequest.getSuccessURL());
        cpeFileWS.setTargetFileName(fileRequest.getTargetFileName());
        final String url = fileRequest.getLink() == null && fileRequest.getFileName() != null && !fileRequest.getUrl().endsWith(fileRequest.getFileName()) ?
                fileRequest.getUrl().endsWith("/") ? fileRequest.getUrl() + fileRequest.getFileName()
                        : fileRequest.getUrl() + "/" + fileRequest.getFileName()
                : fileRequest.getLink();
        cpeFileWS.setUrl(url);
        cpeFileWS.setUsername(fileRequest.getUsername());
        downloadTask.setFile(cpeFileWS);
        return downloadTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.DOWNLOAD_TASK);
        List<ActionParameters> parameters = new ArrayList<>();
        final ServerDetails serverDetails = fileServerService.getServerDetails(ClientType.mc)   /*  ClientType.mc because we can use ug task`s functionality only on management center*/
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        action.getActionDownloadList()
                .forEach(a ->
                        {
                            String link = a.getUrl();
                            String url = link == null || !link.contains("/") ? link : link.substring(0, link.lastIndexOf("/") + 1);
                            String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);
                            parameters.add(
                                    ActionParameters.<DownloadTaskActionResponse>builder()
                                            .name(fileTypeRepository.findNameById(a.getFileTypeId()))
                                            .value(Boolean.TRUE.equals(a.getNewest()) ? "The newest firmware " : a.getUrl())
                                            .details(DownloadTaskActionResponse.builder()
                                                    .delay(a.getDelaySeconds())
                                                    .deliveryMethod(a.getDeliveryMethod() != null
                                                            ? deviceMapper.integerToDeliveryMethod(a.getDeliveryMethod())
                                                            : null)
                                                    .actType(FileActType.DOWNLOAD)
                                                    .deliveryProtocol(a.getDeliveryProtocol() != null
                                                            ? deviceMapper.integerToDeliveryProtocol(a.getDeliveryProtocol())
                                                            : null)
                                                    .url(url)
                                                    .failureUrl(a.getFailureUrl())
                                                    .description(a.getFilename())
                                                    .filename(fileName)
                                                    .fileSize(a.getFileSize())
                                                    .fileTypeId(a.getFileTypeId())
                                                    .newest(a.getNewest())
                                                    .password(a.getPassword())
                                                    .resetSession(a.getResetSession())
                                                    .targetFileName(a.getTargetFileName())
                                                    .link(link)
                                                    .username(a.getUsername())
                                                    .version(a.getVersion())
                                                    .sendBytes(a.getSendBytes())
                                                    .successUrl(a.getSuccessUrl())
                                                    .isManual(serverDetails != null && serverDetails.getAddress() != null
                                                            && !a.getUrl() .contains(serverDetails.getAddress()))
                                                    .build())
                                            .build()
                            );
                        }
                );
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();

        action.getActionDownloadList().forEach(d -> {
            DownloadTask downloadTask = new DownloadTask();
            CpeFileWS cpeFileWS = new CpeFileWS();
            downloadTask.setOrder(action.getPriority());
            cpeFileWS.setDelaySeconds(d.getDelaySeconds());
            cpeFileWS.setFileName(d.getFilename());
            cpeFileWS.setFileSize(d.getFileSize());
            cpeFileWS.setFileTypeId(d.getFileTypeId());
            cpeFileWS.setFileVersion(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSFileVersion(d.getVersion()));
            cpeFileWS.setDeliveryMethod(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryMethod(d.getDeliveryMethod()));
            cpeFileWS.setNewest(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSNewest(d.getNewest()));
            cpeFileWS.setPassword(d.getPassword());
            cpeFileWS.setReset(d.getResetSession());
            cpeFileWS.setDeliveryProtocol(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryProtocol(d.getDeliveryProtocol())); // -1 -> DeliveryProtocol.NotSet
            cpeFileWS.setFailureURL(d.getFailureUrl());
            cpeFileWS.setSendBytes(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSSendBytes(d.getSendBytes()));
            cpeFileWS.setSuccessURL(d.getSuccessUrl());
            cpeFileWS.setTargetFileName(d.getTargetFileName());
            cpeFileWS.setUrl(d.getUrl());
            cpeFileWS.setUsername(d.getUsername());
            downloadTask.setFile(cpeFileWS);
            updateTaskWSList.add(downloadTask);
        });

        return updateTaskWSList;
    }
}
