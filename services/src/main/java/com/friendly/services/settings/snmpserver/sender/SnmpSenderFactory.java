package com.friendly.services.settings.snmpserver.sender;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.SnmpServer;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.snmpserver.SnmpServerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SnmpSenderFactory {

    @NonNull
    private final DomainService domainService;

    @NonNull
    private final SnmpServerService snmpService;

    private final static Map<ClientType, SnmpSender> SENDER_MAP = new HashMap<>();

    @PostConstruct
    private void init() {
        SENDER_MAP.clear();

        SENDER_MAP.putAll(snmpService.getAllSnmpServers(ClientType.mc)
                .stream()
                .collect(Collectors.toMap(SnmpServer::getId,
                        snmp -> new SnmpSender(snmp.getHost(),
                                snmp.getPort(),
                                snmp.getCommunity(),
                                snmp.getVersion()))));
    }

    public SnmpSender getSnmpSender(final ClientType clientType) {
        return SENDER_MAP.get(clientType);
       /* if (snmpSender != null) {
            return snmpSender;
        } else {
            return getSnmpSender(domainService.getParentDomainId(domainId));
        }*/
    }

    public static void putSnmpSender(final SnmpServer snmpServer) {
        SENDER_MAP.put(snmpServer.getId(), new SnmpSender(snmpServer.getHost(),
                snmpServer.getPort(),
                snmpServer.getCommunity(),
                snmpServer.getVersion()));
    }

}
