package com.friendly.services.settings.snmpserver.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.SnmpServer;
import com.friendly.commons.models.settings.request.SnmpServerRequest;
import com.friendly.commons.models.settings.response.SnmpServerResponse;
import com.friendly.services.settings.snmpserver.orm.iotw.model.SnmpServerEntity;
import org.springframework.stereotype.Component;

@Component
public class SnmpServerMapper {

    public SnmpServerEntity snmpServerToEntity(final ClientType clientType,
                                               final SnmpServer snmpServer) {
        return SnmpServerEntity.builder()
                .id(clientType)
                .host(snmpServer.getHost())
                .port(snmpServer.getPort())
                .community(snmpServer.getCommunity())
                .version(snmpServer.getVersion())
                .build();
    }

    public SnmpServerEntity snmpServerToEntity(final ClientType clientType,
                                               final SnmpServerRequest snmpServer) {
        return SnmpServerEntity.builder()
                .id(clientType)
                .host(snmpServer.getHost())
                .port(snmpServer.getPort())
                .community(snmpServer.getCommunity())
                .version(snmpServer.getVersion())
                .build();
    }

    public SnmpServer entityToSnmpServer(final SnmpServerEntity snmpServerEntity) {
        if (snmpServerEntity == null) {
            return null;
        }
        final SnmpServer.SnmpServerBuilder snmpServerBuilder =
                SnmpServer.builder()
                        .id(snmpServerEntity.getId())
                        .host(snmpServerEntity.getHost())
                        .port(snmpServerEntity.getPort())
                        .community(snmpServerEntity.getCommunity())
                        .version(snmpServerEntity.getVersion());
        return snmpServerBuilder.build();
    }

    public SnmpServerResponse entityToSnmpServerResponse(final SnmpServerEntity snmpServerEntity) {
        if (snmpServerEntity == null) {
            return null;
        }
        return SnmpServerResponse.builder()
                .host(snmpServerEntity.getHost())
                .port(snmpServerEntity.getPort())
                .community(snmpServerEntity.getCommunity())
                .version(snmpServerEntity.getVersion()).build();
    }

    public SnmpServerResponse modelToSnmpServerResponse(final SnmpServer snmpServerEntity) {
        if (snmpServerEntity == null) {
            return null;
        }
        return SnmpServerResponse.builder()
                .host(snmpServerEntity.getHost())
                .port(snmpServerEntity.getPort())
                .community(snmpServerEntity.getCommunity())
                .version(snmpServerEntity.getVersion()).build();
    }

    public SnmpServerResponse requestToSnmpServerResponse(final SnmpServerRequest snmpServerEntity) {
        if (snmpServerEntity == null) {
            return null;
        }
        return SnmpServerResponse.builder()
                .host(snmpServerEntity.getHost())
                .port(snmpServerEntity.getPort())
                .community(snmpServerEntity.getCommunity())
                .version(snmpServerEntity.getVersion()).build();
    }

}
