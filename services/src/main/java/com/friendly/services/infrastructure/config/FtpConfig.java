package com.friendly.services.infrastructure.config;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.services.settings.fileserver.FTFtpClient;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.settings.userinterface.InterfaceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@DependsOn({"fileServerService", "interfaceService", "settingInitializer", "liquibaseIotw"})
public class FtpConfig {

    @NonNull
    private final FileServerService fileServerService;

    @NonNull
    private final InterfaceService interfaceService;

    @Bean
    public FTFtpClient mcFtpClient() {
        return getFtpClient(ClientType.mc);
    }

    @Bean
    public FTFtpClient scFtpClient() {
        return getFtpClient(ClientType.sc);
    }

    public FTFtpClient getFtpClient(final ClientType clientType) {
        final ServerDetails serverDetails = fileServerService.getServerDetails(clientType)
                                                             .stream()
                                                             .filter(s -> s.getKey().equals("DownloadFtp"))
                                                             .findAny()
                                                             .orElse(null);
        if (serverDetails == null) {
            return null;
        }
        final List<String> uploadExtensions = interfaceService.getInterfaceValue(clientType, "UploadExtensions")
                                                              .map(e -> Arrays.asList(e.split(", ")))
                                                              .orElse(Collections.singletonList(".cfg"));

        return new FTFtpClient(serverDetails.getAddress(), serverDetails.getUsername(), serverDetails.getPassword(),
                               uploadExtensions);
    }
}
