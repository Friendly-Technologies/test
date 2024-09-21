package com.friendly.services.settings.fileserver.mapper;

import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.settings.fileserver.orm.iotw.model.FileServerEntity;
import com.friendly.services.uiservices.system.orm.iotw.model.ServerDetailsEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileServerMapper {

    private final static Map<String, String> NAME_MAP = new HashMap<>();

    @PostConstruct
    public void putNameMap() {
        NAME_MAP.clear();

        NAME_MAP.put("DownloadHttp", "Download HTTP");
        NAME_MAP.put("DownloadFtp", "Download FTP");
        NAME_MAP.put("UploadHttp", "Upload HTTP");
        NAME_MAP.put("UploadFtp", "Upload FTP");
    }

    public List<ServerDetailsEntity> serverDetailsToFileServerEntities(final List<ServerDetails> details) {
        return details.stream()
                      .map(this::serverDetailToFileServerEntity)
                      .collect(Collectors.toList());
    }

    private ServerDetailsEntity serverDetailToFileServerEntity(final ServerDetails entity) {
        return ServerDetailsEntity.builder()
                                  .id(entity.getId())
                                  .name(entity.getName())
                                  .address(entity.getAddress())
                                  .username(entity.getUsername())
                                  .password(entity.getPassword())
                                  .build();
    }

    public List<ServerDetails> fileServerEntityToFileServer(final FileServerEntity fileServerEntity) {
        return fileServerEntity.getServerDetails()
                               .stream()
                               .map(this::fileServerEntityToFileServer)
                               .collect(Collectors.toList());
    }

    public ServerDetails fileServerEntityToFileServer(final ServerDetailsEntity entity) {
        return ServerDetails.builder()
                            .id(entity.getId())
                            .key(entity.getName())
                            .name(NAME_MAP.get(entity.getName()))
                            .username(entity.getUsername())
                            .address(entity.getAddress())
                            .password(entity.getPassword())
                            .build();
    }

}
