package com.friendly.services.filemanagement.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AddFileBody;
import com.friendly.commons.models.device.DeleteFileBody;
import com.friendly.commons.models.device.DeviceActivity;
import com.friendly.commons.models.device.DeviceFilesBody;
import com.friendly.commons.models.device.FileDownloadNamesBody;
import com.friendly.commons.models.device.FileTypesFilterBody;
import com.friendly.commons.models.device.FileUploadInstancesBody;
import com.friendly.commons.models.device.GetTargetFileNameBody;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.file.AbstractFileRequest;
import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.DeliveryProtocolType;
import com.friendly.commons.models.device.file.DeviceFile;
import com.friendly.commons.models.device.file.DownloadFileDetails;
import com.friendly.commons.models.device.file.FileActType;
import com.friendly.commons.models.device.file.FileDownloadRequest;
import com.friendly.commons.models.device.file.FileTypeFilter;
import com.friendly.commons.models.device.file.FileUploadRequest;
import com.friendly.commons.models.device.response.FileInstancesResponse;
import com.friendly.commons.models.device.response.FileNamesResponse;
import com.friendly.commons.models.device.response.FileTypeFiltersResponse;
import com.friendly.commons.models.device.response.FirmwareStatus;
import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.history.orm.acs.repository.DeviceHistoryRepository;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.device.info.model.FileFtpDto;
import com.friendly.services.device.info.model.FirmwareServerDetails;
import com.friendly.services.device.info.orm.acs.model.CpeEntity;
import com.friendly.services.filemanagement.orm.acs.model.FilesFtpEntity;
import com.friendly.services.filemanagement.orm.acs.repository.FilesFtpRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.filemanagement.orm.acs.model.DeviceFileDownloadEntity;
import com.friendly.services.filemanagement.orm.acs.model.DeviceFileUploadEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionFileEntity;
import com.friendly.services.filemanagement.orm.acs.model.FileTypeEntity;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.filemanagement.orm.acs.repository.DeviceFileDownloadRepository;
import com.friendly.services.filemanagement.orm.acs.repository.DeviceFileUploadRepository;
import com.friendly.services.device.provision.orm.acs.repository.DeviceProvisionFileRepository;
import com.friendly.services.filemanagement.orm.acs.repository.FileTypeRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceLwm2mRepository;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.infrastructure.config.provider.FtpProvider;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.fileserver.FTFtpClient;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.ftacs.CpeFileWS;
import com.ftacs.CpeUploadFile;
import com.ftacs.CpeUploadFileArrayWS;
import com.ftacs.Exception_Exception;
import com.ftacs.FileListWS;
import com.ftacs.IntegerArrayWS;
import com.ftacs.TransactionIdResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.commons.models.device.file.DeliveryMethodType.Pull;
import static com.friendly.commons.models.device.file.DeliveryMethodType.Push;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPS;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPoverTCP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPoverTLS;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.HTTP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.HTTPS;
import static com.friendly.commons.models.device.response.FirmwareStatus.NONE;
import static com.friendly.commons.models.device.response.FirmwareStatus.OK;
import static com.friendly.commons.models.device.response.FirmwareStatus.WARNING;
import static com.friendly.commons.models.reports.DeviceActivityType.ADD_UPLOAD;
import static com.friendly.commons.models.reports.DeviceActivityType.FILE_DELETED;
import static com.friendly.commons.models.reports.DeviceActivityType.FILE_DOWNLOAD;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_DOWNLOAD_FILE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DEVICE_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FILE_FTP_NOT_FOUND;

/**
 * Service that exposes the base functionality for interacting with {@link DeviceActivity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceFileService {

    @NonNull
    private final DeviceMapper deviceMapper;

    @NonNull
    private final UserService userService;

    @NonNull
    private final DomainService domainService;

    @NonNull
    private final StatisticService statisticService;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final ResourceLwm2mRepository resourceLwm2mRepository;

    @NonNull
    private final ParameterService parameterService;

    @NonNull
    private final DeviceFileUploadRepository fileUploadRepository;

    @NonNull
    private final DeviceFileDownloadRepository fileDownloadRepository;

    @NonNull
    private final FileTypeRepository fileTypeRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final DeviceProvisionFileRepository fileRepository;

    @NonNull
    private final FileServerService fileServerService;

    final ProductClassGroupRepository productClassGroupRepository;

    final DeviceHistoryRepository deviceHistoryRepository;
    final FilesFtpRepository filesFtpRepository;

    public FTPage<DeviceFile> getDeviceFile(final String token, DeviceFilesBody body) {
        List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");
        Long deviceId = body.getDeviceId();
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());

        switch (body.getFileActType()) {
            case UPLOAD:
                return getDeviceFileUploadPage(deviceId, pageable, session, user);
            case DOWNLOAD:
                return getDeviceFileDownloadPage(deviceId, pageable, session, user);
            default:
                return null;
        }
    }

    private FTPage<DeviceFile> getDeviceFileUploadPage(final Long deviceId, final List<Pageable> pageable,
                                                       final Session session, final UserResponse user) {
        final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                .stream()
                .filter(s -> s.getKey().equals("UploadHttp"))
                .findAny()
                .orElse(null);
        final List<Page<DeviceFileUploadEntity>> uploadEntity =
                pageable.stream()
                        .map(p -> fileUploadRepository.findAllByDeviceId(deviceId, p))
                        .collect(Collectors.toList());
        final List<DeviceFile> fileUploads =
                uploadEntity.stream()
                        .map(Page::getContent)
                        .flatMap(entities -> entities.stream()
                                .filter(Objects::nonNull)
                                .map(u -> getDeviceFile(session, user, u, serverDetails.getAddress())))
                        .collect(Collectors.toList());
        final FTPage<DeviceFile> uploadPage = new FTPage<>();

        return uploadPage.toBuilder()
                .items(fileUploads)
                .pageDetails(PageUtils.buildPageDetails(uploadEntity))
                .build();
    }

    private FTPage<DeviceFile> getDeviceFileDownloadPage(final Long deviceId, final List<Pageable> pageable,
                                                         final Session session, final UserResponse user) {
        final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        final List<Page<DeviceFileDownloadEntity>> downloadEntity =
                pageable.stream()
                        .map(p -> fileDownloadRepository.findAllByDeviceId(deviceId, p))
                        .collect(Collectors.toList());
        final List<DeviceFile> fileDownloads =
                downloadEntity.stream()
                        .map(Page::getContent)
                        .flatMap(entities -> entities.stream()
                                .filter(Objects::nonNull)
                                .map(u -> getDeviceFileFromDownload(session,
                                        user, u, serverDetails.getAddress())))
                        .collect(Collectors.toList());
        final FTPage<DeviceFile> downloadPage = new FTPage<>();

        return downloadPage.toBuilder()
                .items(fileDownloads)
                .pageDetails(PageUtils.buildPageDetails(downloadEntity))
                .build();
    }

    private DeviceFile getDeviceFile(final Session session, final UserResponse user,
                                     final DeviceFileUploadEntity u, String httpServer) {
        return deviceMapper.deviceFileUploadEntityToDeviceFile(u, session.getClientType(),
                user.getDateFormat(), user.getTimeFormat(), httpServer, session.getZoneId());
    }

    private DeviceFile getDeviceFileFromDownload(final Session session, final UserResponse user,
                                                 final DeviceFileDownloadEntity u, String httpServer) {
        return deviceMapper.deviceFileDownloadEntityToDeviceFile(u, session.getClientType(),
                user.getDateFormat(), user.getTimeFormat(), httpServer, session.getZoneId());
    }

    public FileTypeFiltersResponse getFileTypes(final String token, FileTypesFilterBody body) {
        jwtService.getSession(token);
        Long deviceId = body.getDeviceId();
        FileActType filterType = body.getFileActType();

        final int protocolId =
                cpeRepository.getProtocolTypeByDevice(deviceId)
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, deviceId));
        ProtocolType protocolType = ProtocolType.fromValue(protocolId);
        switch (filterType) {
            case UPLOAD:
                if (protocolType.equals(ProtocolType.TR069) || protocolType.equals(ProtocolType.USP)) {
                    List<FileTypeEntity> entities;
                    if (protocolType.equals(ProtocolType.TR069)) {
                        final List<Integer> ids = new ArrayList<>(Arrays.asList(4, 5));
                        final String root = parameterService.getRootParamName(deviceId);
                        if (parameterService.isParamExistLike(deviceId, root + "DeviceInfo.VendorConfigFile.%.Name")) {
                            ids.add(6);
                        }
                        if (parameterService.isParamExistLike(deviceId, root + "DeviceInfo.VendorLogFile.%.Name")) {
                            ids.add(7);
                        }
                        entities = fileTypeRepository.findAllById(ids);
                    } else {
                        entities = fileTypeRepository.findAllByProtocolId(protocolId);
                    }
                    List<FileTypeFilter> typeFilters = entities
                            .stream()
                            .filter(e -> !e.getName().equals("Deployment File") && !e.getName().equals("Firmware Image"))
                            .map(fileType -> {
                                final Integer id = fileType.getId();
                                final boolean canInstance = id == 6 || id == 7;
                                return FileTypeFilter.builder()
                                        .id(id)
                                        .name(fileType.getName())
                                        .canInstance(canInstance)
                                        .build();
                            })
                            .collect(Collectors.toList());
                    return new FileTypeFiltersResponse(typeFilters);
                }
                return new FileTypeFiltersResponse(Collections.emptyList());
            case DOWNLOAD:
                List<FileTypeFilter> typeFilters = fileTypeRepository.findAllByProtocolId(protocolId)
                        .stream()
                        .filter(fileType -> isFileTypeAllowed(deviceId, fileType))
                        .map(fileType -> {
                            final Integer id = fileType.getId();
                            final boolean canRestore = id == 3;
                            return FileTypeFilter.builder()
                                    .id(id)
                                    .name(fileType.getName())
                                    .canRestore(canRestore)
                                    .build();
                        })
                        .collect(Collectors.toList());
                return new FileTypeFiltersResponse(typeFilters);
            default:
                return new FileTypeFiltersResponse(Collections.emptyList());
        }
    }

    public FileInstancesResponse getInstances(final String token, FileUploadInstancesBody body) {
        jwtService.getSession(token);
        Long deviceId = body.getDeviceId();
        Integer fileTypeId = body.getFileTypeId();

        if (fileTypeId == 6) {
            List<Integer> instances = parameterService.getParamNamesLike(deviceId, "%DeviceInfo.VendorConfigFile.%.")
                    .stream()
                    .map(s -> StringUtils.substringBetween(s, "VendorConfigFile.", "."))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return new FileInstancesResponse(instances);
        }
        if (fileTypeId == 7) {
            List<Integer> instances = parameterService.getParamNamesLike(deviceId, "%DeviceInfo.VendorLogFile.%.")
                    .stream()
                    .map(s -> StringUtils.substringBetween(s, "VendorLogFile.", "."))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return new FileInstancesResponse(instances);
        }
        return new FileInstancesResponse(Collections.emptyList());
    }

    public FileNamesResponse getFileNames(final String token, FileDownloadNamesBody body) {
        final Session session = jwtService.getSession(token);
        Long deviceId = body.getDeviceId();
        Boolean restore = body.getIsRestore();
        Integer fileTypeId = body.getFileTypeId();

        if (restore == null || restore == Boolean.FALSE) {
            final List<Integer> domainIds = domainService.getDomainIdByUserId(session.getUserId())
                    .map(domainService::getChildDomainIds)
                    .orElse(null);
            List<String> fileNames = domainIds == null ? fileTypeRepository.getDeviceActivityTaskNames(deviceId, fileTypeId)
                    .stream().map(o -> new FileFtpDto(
                            (String) o[0],
                            (Timestamp) o[1]
                    ))
                    .sorted(Comparator.comparing(FileFtpDto::getFileDate).reversed())
                    .map(FileFtpDto::getFileName)
                    .collect(Collectors.toList())
                    : fileTypeRepository.getDeviceActivityTaskNames(deviceId, fileTypeId, domainIds)
                    .stream().map(o -> new FileFtpDto(
                            (String) o[0],
                            (Timestamp) o[1]
                    ))
                    .sorted(Comparator.comparing(FileFtpDto::getFileDate).reversed())
                    .map(FileFtpDto::getFileName)
                    .collect(Collectors.toList());
            return new FileNamesResponse(fileNames);
        } else {
            final String serial = cpeRepository.getSerial(deviceId);
            final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
            try (final FTFtpClient ftpClient = FtpProvider.getFtpClient(session.getClientType())) {
                ftpClient.open();
                Map<String, String> urlByName = ftpClient.getConfigFilesBySerial(serial, session.getZoneId(),
                        user.getDateFormat(), user.getTimeFormat());

                Map<String, String> nameByUrl = fileUploadRepository.findAllByDeviceId(deviceId)
                        .stream()
                        .filter(entity -> !StringUtils.isBlank(entity.getFileName()))
                        .filter(entity -> !StringUtils.isBlank(entity.getUrl()))
                        .collect(Collectors.toMap(entity -> entity.getUrl().substring(entity.getUrl().lastIndexOf("/") + 1), DeviceFileUploadEntity::getFileName, (u, n) -> {
                            log.warn("duplicate key found!" + u);
                            return u;
                        }));

                List<String> fileNames = urlByName.keySet()
                        .stream()
                        .sorted()
                        .map(name -> nameByUrl.containsKey(urlByName.get(name)) ? nameByUrl.get(urlByName.get(name)) + " " + name : name)
                        .collect(Collectors.toList());


                return new FileNamesResponse(fileNames);
            } catch (IOException e) {
                return new FileNamesResponse(Collections.emptyList());
            }
        }
    }

    public void addFile(final String token, AddFileBody body) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final AbstractFileRequest fileRequest = body.getFileRequest();
        final Long deviceId = body.getDeviceId();
        final FileActType actType = fileRequest.getActType();
        final Integer priority = 3;

        switch (actType) {
            case DOWNLOAD:
                fileDownload(deviceId, (FileDownloadRequest) fileRequest, clientType, user, priority, session, false);
                break;
            case RESTORE:
                fileDownload(deviceId, (FileDownloadRequest) fileRequest, clientType, user, priority, session, true);
                break;
            case UPLOAD:
            case BACKUP:
                fileUpload(deviceId, (FileUploadRequest) fileRequest, clientType, user, priority);
                break;
        }
    }

    @Transactional
    public void deleteFile(final String token, DeleteFileBody body) {
        final Session session = jwtService.getSession(token);
        final Long deviceId = body.getDeviceId();
        final FileActType fileActType = body.getFileActType();
        final List<Long> ids = body.getFileIds();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();


        //TODO: Change to delete by ACS
        switch (fileActType) {
            case UPLOAD:
                ids.forEach(id -> {
                    final Optional<DeviceFileUploadEntity> upload =
                            fileUploadRepository.findByIdAndDeviceId(id, deviceId);
                    if (upload.isPresent()) {
                        fileUploadRepository.deleteByIdAndDeviceId(id, deviceId);

                        statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                                .userId(session.getUserId())
                                .clientType(session.getClientType())
                                .activityType(FILE_DELETED)
                                .deviceId(deviceId)
                                .groupId(groupId)
                                .serial(serial)
                                .note("fileType=" + upload.get()
                                        .getFileTypeId() +
                                        " url=" + upload.get()
                                        .getUrl())
                                .build());
                        if (body.getDeleteFromFtp()) {
                            try (final FTFtpClient ftpClient = FtpProvider.getFtpClient(session.getClientType())) {
                                ftpClient.open();
                                ftpClient.deleteFile(upload.get().getUrl());
                            } catch (IOException e) {
                                throw new FriendlyIllegalArgumentException(CAN_NOT_DOWNLOAD_FILE, "file not found");
                            }
                        }
                    }
                });
                break;
            case DOWNLOAD:
                ids.forEach(id -> {
                    final Optional<DeviceFileDownloadEntity> download =
                            fileDownloadRepository.findByIdAndDeviceId(id, deviceId);
                    if (download.isPresent()) {
                        fileDownloadRepository.deleteByIdAndDeviceId(id, deviceId);
                        statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                                .userId(session.getUserId())
                                .clientType(session.getClientType())
                                .activityType(FILE_DELETED)
                                .deviceId(deviceId)
                                .groupId(groupId)
                                .serial(serial)
                                .note("fileType=" + download.get()
                                        .getFileTypeId() +
                                        " url=" + download.get()
                                        .getUrl())
                                .build());
                    }
                });
                break;
        }
    }

    private void fileUpload(final Long deviceId, final FileUploadRequest fileRequest, final ClientType clientType,
                            final UserResponse user, final Integer priority) {
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS cpeList = new IntegerArrayWS();
        cpeList.getId().add(deviceId.intValue());
        final CpeUploadFileArrayWS fileList = new CpeUploadFileArrayWS();
        final CpeUploadFile uploadFile = new CpeUploadFile();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        uploadFile.setFileName(fileRequest.getDescription());
        uploadFile.setFileTypeId(fileRequest.getFileTypeId());
        uploadFile.setDelaySeconds(fileRequest.getDelay() == null ? 0 : fileRequest.getDelay());
        uploadFile.setReprovision(fileRequest.getReprovision());
        final String url = fileRequest.getIsManual() == null || fileRequest.getIsManual() == Boolean.FALSE
                ? buildUrl(deviceId, serial, fileRequest.getUrl(), fileRequest.getFileTypeId()) :
                fileRequest.getLink();
        uploadFile.setUrl(url);
        uploadFile.setUsername(fileRequest.getUsername());
        uploadFile.setPassword(fileRequest.getPassword());
        if (fileRequest.getInstance() != null) {
            uploadFile.setInstance(CommonUtils.ACS_OBJECT_FACTORY.createCpeUploadFileInstance(fileRequest.getInstance()));
        }
        fileList.getFile().add(uploadFile);
        try {
            AcsProvider.getAcsWebService(clientType)
                    .uploadFiles(cpeList, fileList, priority, fileRequest.getPush(), null, creator);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(user.getId())
                    .clientType(clientType)
                    .activityType(ADD_UPLOAD)
                    .deviceId(deviceId)
                    .groupId(groupId)
                    .serial(serial)
                    .note("fileType=" + fileRequest.getFileTypeId() +
                            " url=" + url)
                    .build());
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    private String buildFileName(final Long deviceId, String serial) {
//        String url = "http://95.216.70.142:82/uploads/";
        Integer groupId = cpeRepository.getProductClassGroupId(deviceId);
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH-mm-ss")
                .withZone(ZoneId.from(ZoneOffset.UTC));

        return groupId + "_" + serial + "_"
                + dateTimeFormatter.format(Instant.now());
    }

    private String buildUrl(final Long deviceId, String serial, String url, final Integer fileType) {
        final String fileName = buildFileName(deviceId, serial);
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
        }
        return url;
    }

    public Long fileDownload(final Long deviceId, final FileDownloadRequest fileRequest, final ClientType clientType,
                                final UserResponse user, final Integer priority, Session session, boolean isRestore) {
        final Integer domainId = domainService.getDomainIdByUserId(user.getId()).orElse(null);
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();
        final IntegerArrayWS cpeList = new IntegerArrayWS();
        cpeList.getId().add(deviceId != null ? deviceId.intValue() : null);
        final FileListWS files = new FileListWS();
        final CpeFileWS file = new CpeFileWS();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        String fileName = fileRequest.getFileName();
        if (isRestore) {
            try (final FTFtpClient ftpClient = FtpProvider.getFtpClient(session.getClientType())) {
                ftpClient.open();
                fileName = ftpClient.getFileNameForMask(serial, fileName, session.getZoneId(),
                        user.getDateFormat(), user.getTimeFormat());
                if (StringUtils.isEmpty(fileName)) {
                    throw new FriendlyIllegalArgumentException(CAN_NOT_DOWNLOAD_FILE, "file not found");
                }
                fileRequest.setFileName(fileName);
                fileRequest.setLink(null);
            } catch (IOException e) {
                throw new FriendlyIllegalArgumentException(CAN_NOT_DOWNLOAD_FILE, "file not found");
            }

        }
        file.setFileName(fileRequest.getDescription());
        file.setFileTypeId(fileRequest.getFileTypeId());
        file.setFileSize(fileRequest.getFileSize());
        file.setDelaySeconds(fileRequest.getDelay());
        file.setReprovision(fileRequest.getReprovision());
        final String url = fileRequest.getLink() == null && fileName != null && !fileRequest.getUrl().endsWith(fileName) ?
                fileRequest.getUrl().endsWith("/") ? fileRequest.getUrl() + fileRequest.getFileName()
                        : fileRequest.getUrl() + "/" + fileRequest.getFileName()
                : fileRequest.getLink();
        file.setUrl(url);
        file.setUsername(fileRequest.getUsername());
        file.setPassword(fileRequest.getPassword());
        file.setReset(fileRequest.getResetSession());
        file.setSuccessURL(fileRequest.getSuccessURL());
        file.setFailureURL(fileRequest.getFailureURL());
        file.setDeliveryMethod(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryMethod(deviceMapper.deliveryMethodToInteger(
                fileRequest.getDeliveryMethod() != null
                        ? fileRequest.getDeliveryMethod() : DeliveryMethodType.NotSet)));
        DeliveryProtocolType deliveryProtocol = fileRequest.getDeliveryProtocol();
        if (deliveryProtocol != null && !DeliveryProtocolType.NotSet.equals(deliveryProtocol)) {
            file.setDeliveryProtocol(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSDeliveryProtocol(deviceMapper.deliveryProtocolToInteger(
                    deliveryProtocol)));
        }
        file.setFileContent(fileRequest.getFileContent());
        if (fileRequest.getFileVersion() != null)
            file.setFileVersion(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSFileVersion(fileRequest.getFileVersion()));
        file.setTargetFileName(fileRequest.getTargetFileName());
        if (fileRequest.getNewest() != null)
            file.setNewest(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSNewest(fileRequest.getNewest()));
        if (fileRequest.getSendBytes() != null)
            file.setSendBytes(CommonUtils.ACS_OBJECT_FACTORY.createCpeFileWSSendBytes(fileRequest.getSendBytes()));
        files.getFile().add(file);
        try {
            final TransactionIdResponse response =
                    AcsProvider.getAcsWebService(clientType)
                            .downloadFiles(cpeList, files, priority, fileRequest.getPush() != null && fileRequest.getPush(),
                                    null, creator, domainId);

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(user.getId())
                    .clientType(clientType)
                    .activityType(FILE_DOWNLOAD)
                    .deviceId(deviceId)
                    .groupId(groupId)
                    .note("fileType=" + fileRequest.getFileTypeId() +
                            " url=" + url)
                    .serial(serial)
                    .build());
            return response.getTransactionId();
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    private boolean isFileTypeAllowed(final Long deviceId, final FileTypeEntity fileType) {
        int id = fileType.getId();
        if (fileType.getName().equals("LWM2M Resource Definition") ||
                fileType.getName().equals("LWM2M PSK Credentials")
        ) {
            return false;
        } else if(fileType.getName().equals("Software package")) {
            return !resourceLwm2mRepository.getTargetFileNamesForDevice(deviceId.intValue()).isEmpty();
        }
        return (id < 4 || id > 7) &&
                (id != 8 || parameterService.isVoiceProfileEnable(deviceId)) &&
                (id != 9 || parameterService.isVoiceProfileEnable(deviceId)) &&
                id != 2 && id != 14;
    }

    public FileNamesResponse getTargetFileNames(String token, GetTargetFileNameBody body) {
        jwtService.getSession(token);
        List<String> paths;
        if (body.getDeviceId() == null || body.getDeviceId() <= 0) {
            paths = new ArrayList<>();
            for (int i = 0; i <= 9; i++) {
                paths.add(String.format("/9/%d", i));
            }
        } else {
            paths = resourceLwm2mRepository.getTargetFileNamesForDevice(body.getDeviceId());
        }
        return new FileNamesResponse(paths);
    }

    public DownloadFileDetails getFileDetails(final String token, final LongIdRequest request) {
        final Session session = jwtService.getSession(token);
        final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        final DeviceProvisionFileEntity entity = fileRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid file ID"));

        final String link = entity.getUrl();
        String url = link == null || !link.contains("/") ? "" : link.substring(0, link.lastIndexOf("/") + 1);
        String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);

        return DownloadFileDetails.builder()
                .id(entity.getId())
                .priority(entity.getPriority())
                .delay(entity.getDelay())
                .description(entity.getDescription())
                .fileType(entity.getFileType())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .fileSize(entity.getFileSize())
                .deliveryMethod(getDeliveryMethodById(entity.getDeliveryMethod()))
                .deliveryProtocol(getDeliveryProtocolById(entity.getDeliveryProtocol()))
                .url(url)
                .link(link)
                .fileName(fileName)
                .targetFileName(entity.getTargetFileName())
                .isManual(serverDetails != null && serverDetails.getAddress() != null
                        && !url.contains(serverDetails.getAddress()))
                .build();
    }

    public DeliveryMethodType getDeliveryMethodById(Integer deliveryMethod) {
        if(deliveryMethod == null) {
            return DeliveryMethodType.NotSet;
        }
        switch (deliveryMethod) {
            case 0:
                return Pull;
            case 1:
                return Push;
            default:
                return DeliveryMethodType.NotSet;
        }
    }

    public DeliveryProtocolType getDeliveryProtocolById(Integer deliveryProtocol) {
        if(deliveryProtocol == null) {
            return DeliveryProtocolType.NotSet;
        }
        switch (deliveryProtocol) {
            case 0:
                return CoAP;
            case 1:
                return CoAPS;
            case 2:
                return HTTP;
            case 3:
                return HTTPS;
            case 4:
                return CoAPoverTCP;
            case 5:
                return CoAPoverTLS;
            default:
                return DeliveryProtocolType.NotSet;
        }
    }

    public FirmwareServerDetails getFirmwareFileServerDetails(final String fileName, final ClientType ct){
        final ServerDetails serverDetails = fileServerService.getServerDetails(ct)
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
                return FirmwareServerDetails.builder()
                        .url(serverDetails.getAddress())
                        .password(serverDetails.getPassword())
                        .username(serverDetails.getUsername())
                        .build();
    }

    public String getFileTypeById(final Integer id) {
        return fileTypeRepository.findById(id).orElse(null).getName();
    }

    public FirmwareStatus getFirmwareStatus(String token, LongIdRequest request) {
        Session session = jwtService.getSession(token);
        UserResponse userResponse = userService.getUser(token, session.getUserId());
        List<Integer> domainIds = domainService.getDomainIds(userResponse);

        Optional<CpeEntity> cpe = cpeRepository.findById(request.getId());
        if (!cpe.isPresent()) {
            return NONE;
        }

        Integer updates = deviceHistoryRepository.checkIfFirmwareHasUpdate(cpe.get().getId());
        String firmwareVersion = getFirmwareVersion(cpe.get(), domainIds);
        if (updates > 0) {
            return NONE;
        }
        return firmwareVersion.equals(cpe.get().getFirmware()) ? OK : WARNING;
    }

    private String getFirmwareVersion(CpeEntity cpe, List<Integer> domainIds) {
        Long groupId =
                productClassGroupRepository.getGroupIdByProductClassId(cpe.getProductClassId().longValue());
        if (domainIds == null) {
            domainIds = new ArrayList<>();
            domainIds.add(0);
        }
        FilesFtpEntity filesFtpEntity = filesFtpRepository.getNewestFirmwareObj(groupId, domainIds)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(FILE_FTP_NOT_FOUND, groupId));
        return filesFtpEntity.getVersion();
    }

    public FirmwareStatus updateFirmwareVersion (String token, LongIdRequest request) {
        Session session = jwtService.getSession(token);
        UserResponse userResponse = userService.getUser(token, session.getUserId());
        final ClientType clientType = userResponse.getClientType();
        List<Integer> domainIds = domainService.getDomainIds(userResponse);
        if (domainIds == null) {
            domainIds = new ArrayList<>();
            domainIds.add(0);
        }
        CpeEntity cpe = cpeRepository.findById(request.getId())
                .orElseThrow(() -> new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, request.getId()));

        Long groupId = productClassGroupRepository.getGroupIdByProductClassId(cpe.getProductClassId().longValue());

        FilesFtpEntity filesFtpEntity = filesFtpRepository.getNewestFirmwareObj(groupId, domainIds)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(FILE_FTP_NOT_FOUND, groupId));
        fileDownload(cpe.getId(),
                convertFtpFileToFileRequest(filesFtpEntity, clientType),
                clientType, userResponse, 3, session, false);
        return getFirmwareStatus(token, request);
    }

    private FileDownloadRequest convertFtpFileToFileRequest (final FilesFtpEntity filesFtpEntity,
                                                             final ClientType clientType){
        FirmwareServerDetails firmwareFileServerDetails = getFirmwareFileServerDetails(filesFtpEntity.getFileName(), clientType);
        return FileDownloadRequest.builder()
                .fileSize(filesFtpEntity.getFileSize().intValue())
                .fileName(filesFtpEntity.getFileName())
                .description(filesFtpEntity.getFileName())
                .fileVersion(filesFtpEntity.getVersion())
                .newest(filesFtpEntity.getNewest())
                .username(firmwareFileServerDetails.getUsername())
                .password(firmwareFileServerDetails.getPassword())
                .fileTypeId(filesFtpEntity.getFileTypeId())
                .url(firmwareFileServerDetails.getUrl())
                .reprovision(false)
                .build();
    }
}
