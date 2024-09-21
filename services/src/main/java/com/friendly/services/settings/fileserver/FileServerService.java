package com.friendly.services.settings.fileserver;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.file.FileFtp;
import com.friendly.commons.models.file.FileFtpStateEnum;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.commons.models.settings.response.FileServers;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.filemanagement.orm.acs.model.FilesFtpEntity;
import com.friendly.services.settings.fileserver.orm.iotw.model.FileServerEntity;
import com.friendly.services.uiservices.system.orm.iotw.model.ServerDetailsEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.fileserver.orm.iotw.repository.FileServerRepository;
import com.friendly.services.uiservices.system.orm.iotw.repository.ServerDetailsRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.infrastructure.config.provider.FtpProvider;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.fileserver.mapper.FileServerMapper;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.commons.models.websocket.SettingType.FILE_SERVER;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_CONNECT_TO_FTP;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_CONNECT_TO_HTTP;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_DOWNLOAD_FILE;

/**
 * Service that exposes the base functionality for interacting with {@link UserResponse} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServerService {
    private final UserRepository userRepository;

    @NonNull
    private final FileServerRepository fileServerRepository;

    @NonNull
    private final ServerDetailsRepository serverDetailsRepository;

    @NonNull
    private final FileServerMapper fileServerMapper;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final WsSender wsSender;

    @NonNull
    private final DomainService domainService;

    /**
     * Get File Server Setting
     *
     * @return File Server setting
     */
    public FileServers getFileServerSetting(final String token) {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final Optional<UserEntity> user = userRepository.findById(userId);
        Integer domainId = user.get().getDomainId();

        List<ServerDetails> specificServers = null;

        final List<ServerDetails> defaultServers = getFileServers(session.getClientType(), 0);
        if(domainId != 0) {
            specificServers = getFileServers(session.getClientType(), domainId);
        }

        return new FileServers(defaultServers, (specificServers == null || specificServers.isEmpty())
                ? null : specificServers);
    }

    private List<ServerDetails> getFileServers(final ClientType clientType, final Integer domainId) {
        final FileServerEntity fileServerEntity = fileServerRepository.getFileServerEntity(clientType, domainId);
        if (fileServerEntity == null) {
            return null;
        }
        return fileServerMapper.fileServerEntityToFileServer(fileServerEntity);
    }

    public List<ServerDetails> getServerDetails(final ClientType clientType) {
        return fileServerMapper.fileServerEntityToFileServer(fileServerRepository.getFileServerEntity(clientType, 0));
    }

    /**
     * Update Server Detail Setting
     */
    @Transactional
    public void updateServerDetailsSetting(final String token, final List<ServerDetails> serverDetails) {
        final Session session = jwtService.getSession(token);
        boolean containsFtp = false;
        for (ServerDetails item : serverDetails) {
            String name = item.getName();
            if (name == null) {
                continue;
            }
            if (name.endsWith("FTP")) {
                if (!item.getAddress().endsWith("/")) {
                    throw new FriendlyIllegalArgumentException(CAN_NOT_CONNECT_TO_FTP);
                }
                FTFtpClient ftFtpClient = new FTFtpClient(item.getAddress(), item.getUsername(), item.getPassword(),
                        null);
                try {
                    ftFtpClient.open();
                } catch (IOException e) {
                    throw new FriendlyIllegalArgumentException(CAN_NOT_CONNECT_TO_FTP);
                }
                containsFtp = true;
            } else if (name.endsWith("HTTP")) {
                String url = item.getAddress();
                if(isUrlNotValid(url)) {
                    throw new FriendlyIllegalArgumentException(CAN_NOT_CONNECT_TO_HTTP, url);
                }
            }
        }

        final Long userId = session.getUserId();
        final Optional<UserEntity> user = userRepository.findById(userId);
        Integer domainId = user.get().getDomainId();

        final Long serverDetailsId;
        final Optional<FileServerEntity> fileServer = fileServerRepository.findByClientTypeAndDomainId(session.getClientType(), domainId);

        if(fileServer.isPresent()) {
             serverDetailsId = fileServer.get().getId();
        } else {
            serverDetailsId = fileServerRepository.saveAndFlush(FileServerEntity.builder()
                            .clientType(session.getClientType())
                            .domainId(domainId)
                    .build()).getId();
        }

        final List<ServerDetailsEntity> serverDetailsEntities =
                fileServerMapper.serverDetailsToFileServerEntities(serverDetails);

        if (serverDetails.isEmpty() && !(checkIfIdIsForDomain(serverDetailsId))) {
            serverDetailsRepository.deleteAllByServerDetailsId(serverDetailsId);
            return;
        }

        serverDetailsEntities.forEach(s -> {
            String newName = getFileServerNameByKey(s.getName());
            s.setName(newName);
            s.setServerDetailsId(serverDetailsId);
            Optional<ServerDetailsEntity> optional =
                    s.getId() == null ? Optional.empty() : serverDetailsRepository.findById(s.getId());

            if(optional.isPresent() && isNull(s)) {
                serverDetailsRepository.delete(optional.get());
                return;
            }

            if(optional.isPresent()) {
                if(checkIfIdIsForDomain(optional.get().getServerDetailsId())) {
                    s.setServerDetailsId(optional.get().getServerDetailsId());
                }
                ServerDetailsEntity serverDetailsEntity = optional.get();
                setEntityParams(s, serverDetailsEntity);
                serverDetailsRepository.save(serverDetailsEntity);
            } else {
                serverDetailsRepository.save(s);
            }

            wsSender.sendSettingEvent(session.getClientType(), UPDATE,
                    FILE_SERVER, fileServerMapper.fileServerEntityToFileServer(s));
        });

        if (containsFtp) {
            FtpProvider.updateServerSettings();
        }
    }

    public static boolean isUrlValid(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isUrlNotValid(String urlString) {
        return !isUrlValid(urlString);
    }

    private boolean checkIfIdIsForDomain(Long serverDetailsId) {
        Integer domainIdByServerDetailsId = fileServerRepository.getDomainIdByServerDetailsId(serverDetailsId);
        return domainIdByServerDetailsId != null && domainIdByServerDetailsId == 0;
    }

    private boolean isNull(ServerDetailsEntity s) {
        return s.getAddress() == null
                && s.getName() == null
                && s.getUsername() == null
                && s.getPassword() == null;
    }

    private static void setEntityParams(final ServerDetailsEntity s, final ServerDetailsEntity serverDetailsEntity) {
        serverDetailsEntity.setServerDetailsId(s.getServerDetailsId());
        serverDetailsEntity.setName(s.getName());
        serverDetailsEntity.setPassword(s.getPassword());
        serverDetailsEntity.setAddress(s.getAddress());
        serverDetailsEntity.setUsername(s.getUsername());
    }


    public String getDomainFolder(Integer domainId, ClientType clientType) {
        int domainMode = domainService.getDomainMode(clientType);
        if (domainMode > 0 && domainId > 0) {
            String domain = domainService.getDomainNameById(domainId);
            if (domain == null) {
                return "";
            }
            if (domainMode == 1) {
                return domain.replace(".", "/") + "/";
            } else {
                return domain + "/";
            }
        } else {
            return "";
        }
    }

    public List<FileFtp> convertEntitiesToModel(List<FilesFtpEntity> entities, ClientType clientType,
                                                final String zoneId, final String dateFormat, final String timeFormat) {
        boolean domainEnable = domainService.isDomainsEnabled(clientType);
        Map<Integer, List<FilesFtpEntity>> entitiesPerDomain;
        if (domainEnable) {
            entitiesPerDomain = entities.stream().collect(Collectors.groupingBy(entity -> entity.getDomainId() == null ? 0 : entity.getDomainId()));
        } else {
            entitiesPerDomain = Collections.singletonMap(0, entities);
        }
        FTFtpClient ftpClient = null;
        try {
            try {
                ftpClient = FtpProvider.getFtpClient(clientType);
                ftpClient.open();
            } catch (IOException e) {
                ftpClient = null;
                log.error(e.getMessage(), "no connection to ftp");
            }


            List<FileFtp> list = new ArrayList<>();
            FTFtpClient finalFtpClient = ftpClient;
            entitiesPerDomain.keySet().forEach(domainId -> {
                FTPFile[] ftpFiles = new FTPFile[0];
                FileFtpStateEnum state = finalFtpClient == null ? FileFtpStateEnum.Error : FileFtpStateEnum.Absent;
                if (finalFtpClient != null) {
                    try {
                        ftpFiles = finalFtpClient.getFilesFromLocation(getDomainFolder(domainId, clientType));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        state = FileFtpStateEnum.Error;
                    }
                }
                FileFtpStateEnum finalState = state;
                FTPFile[] finalFtpFiles = ftpFiles;
                list.addAll(entitiesPerDomain.get(domainId).stream().map(entity -> {
                    FileFtp fileFtp = FileFtp.builder()
                            .fileName(entity.getFileName())
                            .created(DateTimeUtils.format(entity.getFileDate(), zoneId, dateFormat, timeFormat))
                            .createdIso(entity.getFileDate())
                            .manufacturer(entity.getProductClassGroup().getManufacturerName())
                            .model(entity.getProductClassGroup().getModel())
                            .protocolType(ProtocolType.fromValue(entity.getProductClassGroup().getProtocolId() == null
                                    ? 0
                                    : entity.getProductClassGroup().getProtocolId()))
                            .newest(entity.getNewest() != null && entity.getNewest())
                            .size(entity.getFileSize())
                            .domainName(entity.getDomainName())
                            .fileTypeId(entity.getFileTypeId())
                            .state(finalState)
                            .version(entity.getVersion())
                            .build();


                    Arrays.stream(finalFtpFiles).filter(ftpFile -> ftpFile.getName().equals(entity.getFileName()))
                            .forEach(ftpFile -> {
                                if (ftpFile.getSize() > 0) {
                                    fileFtp.setState(FileFtpStateEnum.Exists);
                                }
                                fileFtp.setSize(ftpFile.getSize());
                            });


                    return fileFtp;
                }).collect(Collectors.toList()));

            });
            return list;
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }


    }

    public String addFile(Integer domainId, ClientType clientType, MultipartFile file) {
        try (final FTFtpClient ftpClient = FtpProvider.getFtpClient(clientType)) {
            ftpClient.open();
            String location = getDomainFolder(domainId, clientType);
            boolean added = ftpClient.addFile(location, file);
            final ServerDetails serverDetails = getServerDetails(clientType)
                    .stream()
                    .filter(s -> s.getKey().equals("DownloadHttp"))
                    .findAny()
                    .orElse(null);
            return added ? serverDetails.getAddress() + location + file.getOriginalFilename() : null;
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(CAN_NOT_DOWNLOAD_FILE, "file not found");
        }
    }

    private static String getFileServerNameByKey(final String name) {
        switch (name) {
            case "Download HTTP":
                return "DownloadHttp";
            case "Download FTP":
                return "DownloadFtp";
            case "Upload HTTP":
                return "UploadHttp";
            case "Upload FTP":
                return "UploadFtp";
            default:
                return name;
        }
    }

}
