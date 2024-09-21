package com.friendly.services.filemanagement.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.file.FileActType;
import com.friendly.commons.models.device.file.FileTypeFilter;
import com.friendly.commons.models.device.response.FileInstancesResponse;
import com.friendly.commons.models.device.response.FileNamesResponse;
import com.friendly.commons.models.device.response.FileTypeFiltersResponse;
import com.friendly.commons.models.file.FileExistResponse;
import com.friendly.commons.models.file.FileFtp;
import com.friendly.commons.models.file.FileFtpKey;
import com.friendly.commons.models.file.FileFtpStateEnum;
import com.friendly.commons.models.file.FilesFtpBody;
import com.friendly.commons.models.file.FilesFtpTypesBody;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.filemanagement.orm.acs.model.FileTypeEntity;
import com.friendly.services.filemanagement.orm.acs.model.FilesFtpEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.filemanagement.orm.acs.repository.FileTypeRepository;
import com.friendly.services.filemanagement.orm.acs.repository.FilesFtpRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.infrastructure.config.provider.FtpProvider;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.fileserver.FTFtpClient;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_DOWNLOAD_FILE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FILE_FTP_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PRODUCT_CLASS_GROUP_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {
    @NonNull FilesFtpRepository filesFtpRepository;
    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final DomainService domainService;
    @NonNull
    private final FileServerService fileServerService;
    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;
    @NonNull
    private final FileTypeRepository fileTypeRepository;
    @NonNull
    private final TemplateService templateService;


    public FTPage<FileFtp> getList(String token, FilesFtpBody filesFtpBody) {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final List<Integer> domainIds = domainService.getDomainIds(user);
        final List<Pageable> pageable = PageUtils.createPageRequest(filesFtpBody.getPageNumbers(),
                filesFtpBody.getPageSize(), null, "fileName");

        final List<Page<FilesFtpEntity>> listFromDB =
                pageable.stream()
                        .map(p -> filesFtpRepository.findAllForListView(domainIds,
                                filesFtpBody.getProtocolType() == null ? null : filesFtpBody.getProtocolType().getValue(),
                                filesFtpBody.getFileTypeId(), filesFtpBody.getModel(), filesFtpBody.getManufacturer(), p))
                        .collect(Collectors.toList());
        final FTPage<FileFtp> fileFtpPage = new FTPage<>();
        return fileFtpPage.toBuilder()
                .pageDetails(PageUtils.buildPageDetails(listFromDB))
                .items(listFromDB.stream()
                        .map(Page::getContent)
                        .flatMap(r -> fileServerService.convertEntitiesToModel(r,
                                        session.getClientType(),
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList()))
                .build();
    }

    public FileNamesResponse getFileNames(final String token, final String manufacturer, final String model, final Integer fileTypeId) {
        jwtService.getSession(token);
        ProductClassGroupEntity productClassGroup = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(PRODUCT_CLASS_GROUP_NOT_FOUND, manufacturer, model));
        final Long groupId = productClassGroup.getId();
        List<FilesFtpEntity> filesFtpEntities = filesFtpRepository.findAllByGroupIdAndFileTypeId(groupId, fileTypeId);
        List<String> fileNames = filesFtpEntities.stream()
                .map(FilesFtpEntity::getFileName)
                .collect(Collectors.toList());
        return new FileNamesResponse(fileNames);
    }


    public FileTypeFiltersResponse getFileTypes(String token, FilesFtpTypesBody filesFtpTypesBody) {
        jwtService.getSession(token);
        List<FileTypeEntity> list;
        if (StringUtils.isEmpty(filesFtpTypesBody.getModel()) || StringUtils.isEmpty(filesFtpTypesBody.getManufacturer())) {
            list = fileTypeRepository.findAll();
            return getFilterDefaultFileTypes(list);
        } else{
            ProtocolType protocolType = ProtocolType.fromValue(productClassGroupRepository.getProtocolIdByManufacturerAndModel(filesFtpTypesBody.getManufacturer(), filesFtpTypesBody.getModel()));
            return getFileTypeFiltersResponseByFileActType(protocolType, filesFtpTypesBody.getFileActType(),
                    filesFtpTypesBody.getManufacturer(), filesFtpTypesBody.getModel());
        }
    }

    private FileTypeFiltersResponse getFileTypeFiltersResponseByFileActType(ProtocolType protocolType, final FileActType fileActType,
                                                                            final String manufacturer, final String model) {
        ProductClassGroupEntity productClassGroup = productClassGroupRepository.findByManufacturerNameAndModelAndProtocolId(manufacturer, model, protocolType.getValue());
        boolean voip = templateService.isVoiceProfileEnableInTemplate(productClassGroup.getId());
        switch (fileActType) {
            case UPLOAD:
                if (protocolType.equals(ProtocolType.TR069) || protocolType.equals(ProtocolType.USP)) {
                    List<FileTypeEntity> entities;
                    if (protocolType.equals(ProtocolType.TR069)) {
                        final List<Integer> ids = new ArrayList<>(Arrays.asList(4, 5));
                        final String root = templateService.getRootParamName(productClassGroup.getId());
                        if (Boolean.TRUE.equals(productClassGroupRepository.isParamExistLikeForManufacturerAndModel(manufacturer, model, root + "DeviceInfo.VendorConfigFile.%.Name"))) {
                            ids.add(6);
                        }
                        if (Boolean.TRUE.equals(productClassGroupRepository.isParamExistLikeForManufacturerAndModel(manufacturer, model, root + "DeviceInfo.VendorLogFile.%.Name"))) {
                            ids.add(7);
                        }
                        entities = fileTypeRepository.findAllById(ids);
                    } else {
                        entities = fileTypeRepository.findAllByProtocolId(protocolType.getValue());
                    }
                    List<FileTypeFilter> typeFilters = entities.stream()
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
                List<FileTypeEntity> typeFilters = fileTypeRepository.findAllByProtocolId(protocolType.getValue());
                return getFilteredFileTypes(typeFilters, voip);
            default:
                return new FileTypeFiltersResponse(Collections.emptyList());
        }
    }


    private static FileTypeFiltersResponse getFilteredFileTypes(List<FileTypeEntity> list, boolean voip) {
        return new FileTypeFiltersResponse(list.stream().filter(fileTypeEntity ->
                        !fileTypeEntity.getName().contains("Log")   // Vendor Log
                                && fileTypeEntity.getId() != 2      //  2 = "Web content"
                                && (fileTypeEntity.getId() < 4 || fileTypeEntity.getId() > 7) // 4 = "Vendr Confguration File, 5 = "Vendor Log file", 6 = "Vendor ConfigurationFile <i>", 7 = "Vendor Log File<i>"
                                && (fileTypeEntity.getId() < 10 || fileTypeEntity.getId() > 11) // 10 = "LWM2M Resource Definition", 11 = "LWM2M PSK Credentials"
                                && (voip || (!voip && fileTypeEntity.getId() != 8 && fileTypeEntity.getId() != 9))) // 8 = "Tone File" - for VoIP only, 9 = "Ringer File" - for VoIP only
                .map(fileTypeEntity -> FileTypeFilter.builder()
                        .name(fileTypeEntity.getName())
                        .id(fileTypeEntity.getId())
                        .canInstance(false)
                        .canRestore(false)
                        .protocolType(ProtocolType.fromValue(fileTypeEntity.getProtocolId() == null
                                ? 0
                                : fileTypeEntity.getProtocolId()))
                        .build())
                .collect(Collectors.toList()));
    }

    private static FileTypeFiltersResponse getFilterDefaultFileTypes(List<FileTypeEntity> list) {
        return new FileTypeFiltersResponse(list.stream()
                .filter(fileTypeEntity ->
                        !fileTypeEntity.getName().contains("Log")  // Vendor Log
                                && (fileTypeEntity.getId() < 4 || fileTypeEntity.getId() > 7) // 4 = "Vendr Confguration File, 5 = "Vendor Log file", 6 = "Vendor ConfigurationFile <i>", 7 = "Vendor Log File<i>"
                                && (fileTypeEntity.getId() < 10 || fileTypeEntity.getId() > 11)) // 10 = "LWM2M Resource Definition", 11 = "LWM2M PSK Credentials"
                .map(fileTypeEntity -> FileTypeFilter.builder()
                        .name(fileTypeEntity.getName())
                        .id(fileTypeEntity.getId())
                        .canInstance(false)
                        .canRestore(false)
                        .protocolType(ProtocolType.fromValue(fileTypeEntity.getProtocolId() == null
                                ? 0
                                : fileTypeEntity.getProtocolId()))
                        .build())
                .collect(Collectors.toList()));
    }

    public FileFtp editFile(String token, FileFtp fileFtp) {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        ProductClassGroupEntity productClassGroup;
        if (fileFtp.getProtocolType() != null) {
            productClassGroup = productClassGroupRepository.findByManufacturerNameAndModelAndProtocolId(
                    fileFtp.getManufacturer(), fileFtp.getModel(), fileFtp.getProtocolType().getValue());
        } else {
            List<ProductClassGroupEntity> productClassGroups = productClassGroupRepository.findAllByManufacturerNameAndModel(
                    fileFtp.getManufacturer(), fileFtp.getModel());
            productClassGroup = productClassGroups.isEmpty() ? null : productClassGroups.get(0);
        }

        // update
        List<Integer> domainIds = domainService.getDomainIds(user);
        FilesFtpEntity entity = (domainIds == null ?
                filesFtpRepository.findByFileNameAndGroupId(fileFtp.getFileName(), productClassGroup.getId())
                : filesFtpRepository.findByFileNameAndGroupIdAndDomainIdIn(fileFtp.getFileName(), productClassGroup.getId(), domainIds)
        ).orElse(null);

        if (entity == null) {
            throw new FriendlyIllegalArgumentException(FILE_FTP_NOT_FOUND, fileFtp.getFileName());
        }

        entity.setNewest(fileFtp.getNewest());
        entity.setFileTypeId(fileFtp.getFileTypeId());
        entity.setVersion(fileFtp.getVersion());
        filesFtpRepository.saveAndFlush(entity);

        return fileFtp;
    }

    public FileFtp addFile(String token, FileFtp fileFtp, MultipartFile file) {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());

        ProductClassGroupEntity productClassGroup;
        if (fileFtp.getProtocolType() != null) {
            productClassGroup = productClassGroupRepository.findByManufacturerNameAndModelAndProtocolId(
                    fileFtp.getManufacturer(), fileFtp.getModel(), fileFtp.getProtocolType().getValue());
        } else {
            List<ProductClassGroupEntity> productClassGroups = productClassGroupRepository.findAllByManufacturerNameAndModel(
                    fileFtp.getManufacturer(), fileFtp.getModel());
            productClassGroup = productClassGroups.isEmpty() ? null : productClassGroups.get(0);
        }
        FilesFtpEntity entity;
        // create

        String url = fileServerService.addFile(user.getDomainId(), session.getClientType(), file);
        fileFtp.setState(FileFtpStateEnum.Error);
        if (url != null) {
            entity = new FilesFtpEntity();

            fileFtp.setState(FileFtpStateEnum.Exists);
            fileFtp.setSize(file.getSize());
            fileFtp.setFileUrl(url);
            fileFtp.setFileName(file.getOriginalFilename());

            entity.setFileDate(Instant.now());
            entity.setFileName(fileFtp.getFileName());
            entity.setDomainId(user.getDomainId());
            entity.setGroupId(productClassGroup.getId());
            entity.setProductClassGroup(productClassGroup);
            entity.setFileSize(file.getSize());
            entity.setNewest(fileFtp.getNewest());
            entity.setFileTypeId(fileFtp.getFileTypeId());
            entity.setVersion(fileFtp.getVersion());

            filesFtpRepository.saveAndFlush(entity);
        }
        return fileFtp;
    }

    public FileFtp getFile(String token, FileFtpKey fileFtpKey) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        List<Integer> domainIds = domainService.getDomainIds(user);
        ProductClassGroupEntity productClassGroup;
        if (fileFtpKey.getProtocolType() != null) {
            productClassGroup = productClassGroupRepository.findByManufacturerNameAndModelAndProtocolId(
                    fileFtpKey.getManufacturer(), fileFtpKey.getModel(), fileFtpKey.getProtocolType().getValue());
        } else {
            List<ProductClassGroupEntity> productClassGroups = productClassGroupRepository.findAllByManufacturerNameAndModel(
                    fileFtpKey.getManufacturer(), fileFtpKey.getModel());
            productClassGroup = productClassGroups.isEmpty() ? null : productClassGroups.get(0);
        }
        FilesFtpEntity entity = (domainIds == null ?
                filesFtpRepository.findByFileNameAndGroupId(fileFtpKey.getFileName(), productClassGroup.getId())
                : filesFtpRepository.findByFileNameAndGroupIdAndDomainIdIn(fileFtpKey.getFileName(), productClassGroup.getId(), domainIds)
        ).orElse(null);
        if (entity == null) {
            return null;
        }

        final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        String location = fileServerService.getDomainFolder(entity.getDomainId(), session.getClientType());
        return FileFtp.builder()
                .fileName(entity.getFileName())
                .created(DateTimeUtils.format(entity.getFileDate(), session.getZoneId(), user.getDateFormat(), user.getTimeFormat()))
                .createdIso(entity.getFileDate())
                .manufacturer(entity.getProductClassGroup().getManufacturerName())
                .model(entity.getProductClassGroup().getModel())
                .version(entity.getVersion())
                .newest(entity.getNewest() != null && entity.getNewest())
                .size(entity.getFileSize())
                .domainName(entity.getDomainName())
                .fileTypeId(entity.getFileTypeId())
                .fileUrl(serverDetails.getAddress() + location + entity.getFileName())
                .build();
    }

    public void deleteFile(String token, List<FileFtpKey> files) {
        Session session = jwtService.getSession(token);
        List<Integer> domainIds = getDomainIds(session);
        List<FilesFtpEntity> entities = files.stream().map(fileFtpKey -> {
            ProductClassGroupEntity productClassGroup;
            if (fileFtpKey.getProtocolType() != null) {
                productClassGroup = productClassGroupRepository.findByManufacturerNameAndModelAndProtocolId(
                        fileFtpKey.getManufacturer(), fileFtpKey.getModel(), fileFtpKey.getProtocolType().getValue());
            } else {
                List<ProductClassGroupEntity> productClassGroups = productClassGroupRepository.findAllByManufacturerNameAndModel(
                        fileFtpKey.getManufacturer(), fileFtpKey.getModel());
                productClassGroup = productClassGroups.isEmpty() ? null : productClassGroups.get(0);
            }
            return (domainIds == null ?
                    filesFtpRepository.findByFileNameAndGroupId(fileFtpKey.getFileName(), productClassGroup.getId())
                    : filesFtpRepository.findByFileNameAndGroupIdAndDomainIdIn(fileFtpKey.getFileName(), productClassGroup.getId(), domainIds)
            ).orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (entities.isEmpty()) {
            return;
        }
        filesFtpRepository.deleteAll(entities);
        List<String> failureFileNames = new ArrayList<>();
        entities.forEach(entity -> {
            try (final FTFtpClient ftpClient = FtpProvider.getFtpClient(session.getClientType())) {
                ftpClient.open();
                ftpClient.deleteFile(fileServerService.getDomainFolder(entity.getDomainId(), session.getClientType()) + entity.getFileName());
            } catch (IOException e) {
                failureFileNames.add(entity.getFileName());
            }
        });
        if (!failureFileNames.isEmpty()) {
            throw new FriendlyIllegalArgumentException(CAN_NOT_DOWNLOAD_FILE,
                    "files not found: " + StringUtils.collectionToCommaDelimitedString(failureFileNames));
        }
    }

    private List<Integer> getDomainIds(Session session) {
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        return domainService.getDomainIds(user);
    }

    public FileExistResponse isFileExist(String token, FileFtpKey fileFtpKey) {
        final Session session = jwtService.getSession(token);
        List<Integer> domainIds = getDomainIds(session);
        ProductClassGroupEntity productClassGroup;
        if (fileFtpKey.getProtocolType() != null) {
            productClassGroup = productClassGroupRepository.findByManufacturerNameAndModelAndProtocolId(
                    fileFtpKey.getManufacturer(), fileFtpKey.getModel(), fileFtpKey.getProtocolType().getValue());
        } else {
            List<ProductClassGroupEntity> productClassGroups = productClassGroupRepository.findAllByManufacturerNameAndModel(
                    fileFtpKey.getManufacturer(), fileFtpKey.getModel());
            productClassGroup = productClassGroups.isEmpty() ? null : productClassGroups.get(0);
        }
        return FileExistResponse.builder().exist(
                (domainIds == null ?
                        filesFtpRepository.findByFileNameAndGroupId(fileFtpKey.getFileName(), productClassGroup.getId())
                        : filesFtpRepository.findByFileNameAndGroupIdAndDomainIdIn(fileFtpKey.getFileName(), productClassGroup.getId(), domainIds)
                ).isPresent()).build();
    }

    public FileInstancesResponse getFileInstancesByManufacturerAndModel(String token, String manufacturer, String model, Integer fileTypeId) {
        jwtService.getSession(token);
        Long groupId = productClassGroupRepository.findFirstByManufacturerNameAndModelOrderById(manufacturer, model)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(PRODUCT_CLASS_GROUP_NOT_FOUND, manufacturer, model)).getId();
        if (fileTypeId == 6) {
            List<Integer> instances = templateService.getParamNamesLike(groupId, "%DeviceInfo.VendorConfigFile.%.")
                    .stream()
                    .map(s -> org.apache.commons.lang3.StringUtils.substringBetween(s, "VendorConfigFile.", "."))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return new FileInstancesResponse(instances);
        }
        if (fileTypeId == 7) {
            List<Integer> instances = templateService.getParamNamesLike(groupId, "%DeviceInfo.VendorLogFile.%.")
                    .stream()
                    .map(s -> org.apache.commons.lang3.StringUtils.substringBetween(s, "VendorLogFile.", "."))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return new FileInstancesResponse(instances);
        }
        return new FileInstancesResponse(Collections.emptyList());
    }
}