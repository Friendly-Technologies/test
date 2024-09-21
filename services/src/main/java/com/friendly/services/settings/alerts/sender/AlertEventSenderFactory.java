package com.friendly.services.settings.alerts.sender;

import com.friendly.commons.models.auth.ClientType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AlertEventSenderFactory {
    private final static Map<ClientType, AlertEventSender> SENDER_MAP = new HashMap<>();

    public AlertEventSenderFactory(@Qualifier("mcAlertEventSender") AlertEventSender mcAlertEventSender,
            @Qualifier("scAlertEventSender") AlertEventSender scAlertEventSender) {
        SENDER_MAP.put(ClientType.sc, scAlertEventSender);
        SENDER_MAP.put(ClientType.mc, mcAlertEventSender);
    }

    public AlertEventSender getAlertEventSender(final ClientType clientType) {
        return SENDER_MAP.get(clientType);
    }
}
