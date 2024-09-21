package com.friendly.services.infrastructure.config.provider;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.config.FtpConfig;
import com.friendly.services.settings.fileserver.FTFtpClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@DependsOn("ftpConfig")
public class FtpProvider {

    private static final Map<ClientType, FTFtpClient> ftpClientMap = new HashMap<>();

    static FtpConfig ftpConfigStatic;


    @NonNull
    FtpConfig ftpConfig;

    @PostConstruct
    private void init() {
        ftpConfigStatic = ftpConfig;
        updateServerSettings();
    }

    public static FTFtpClient getFtpClient(final ClientType clientType) {
        return ftpClientMap.get(clientType);
    }


    public static void updateServerSettings() {
        ftpClientMap.put(ClientType.mc, ftpConfigStatic.mcFtpClient());
        ftpClientMap.put(ClientType.sc, ftpConfigStatic.scFtpClient());
    }
}
