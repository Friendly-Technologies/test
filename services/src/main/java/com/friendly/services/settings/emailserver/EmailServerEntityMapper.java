package com.friendly.services.settings.emailserver;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.EmailServer;
import com.friendly.services.settings.emailserver.orm.iotw.model.EmailServerEntity;
import com.friendly.services.settings.emailserver.orm.iotw.model.EmailServerSpecificEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailServerEntityMapper {

    public EmailServerEntity toEntity(final Integer domainId,
                                      final ClientType clientType,
                                      final EmailServer emailServer) {
        return EmailServerEntity.builder()
                .id(clientType)
                .domainId(domainId)
                .username(emailServer.getUsername())
                .password(emailServer.getPassword())
                .host(emailServer.getHost())
                .port(emailServer.getPort())
                .from(emailServer.getFrom())
                .subject(emailServer.getSubject())
                .enableSSL(emailServer.isEnableSSL())
                .build();
    }

    public EmailServerSpecificEntity toSpecificEntity(final Integer id,
                                                      final Integer domainId,
                                                      final ClientType clientType,
                                      final EmailServer emailServer) {
        return EmailServerSpecificEntity.builder()
                .id(id)
                .domainId(domainId)
                .clientType(clientType)
                .username(emailServer.getUsername())
                .password(emailServer.getPassword())
                .host(emailServer.getHost())
                .port(emailServer.getPort())
                .from(emailServer.getFrom())
                .subject(emailServer.getSubject())
                .enableSSL(emailServer.isEnableSSL())
                .build();
    }

    public EmailServer fromEntity(final EmailServerEntity emailServerEntity) {
        if (emailServerEntity == null) {
            return null;
        }
        final EmailServer.EmailServerBuilder emailServerBuilder =
                EmailServer.builder()
                        .username(emailServerEntity.getUsername())
                        .password(emailServerEntity.getPassword())
                        .host(emailServerEntity.getHost())
                        .port(emailServerEntity.getPort())
                        .from(emailServerEntity.getFrom())
                        .subject(emailServerEntity.getSubject())
                        .enableSSL(emailServerEntity.isEnableSSL());
        return emailServerBuilder.build();
    }

    public EmailServer fromEntity(final EmailServerSpecificEntity emailServerEntity) {
        if (emailServerEntity == null) {
            return null;
        }
        final EmailServer.EmailServerBuilder emailServerBuilder =
                EmailServer.builder()
                        .username(emailServerEntity.getUsername())
                        .password(emailServerEntity.getPassword())
                        .host(emailServerEntity.getHost())
                        .port(emailServerEntity.getPort())
                        .from(emailServerEntity.getFrom())
                        .subject(emailServerEntity.getSubject())
                        .enableSSL(emailServerEntity.isEnableSSL());
        return emailServerBuilder.build();
    }

}
