package com.friendly.services.settings.alerts.config;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.utils.mail.EmailSender;
import com.friendly.services.settings.alerts.AlertsService;
import com.friendly.services.settings.alerts.sender.AlertEventSender;
import com.friendly.services.settings.snmpserver.sender.SnmpSenderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlertConfig {

    @Bean
    public AlertEventSender mcAlertEventSender(AlertsService alertsService, SnmpSenderFactory snmpSenderFactory,
                                               EmailSender emailSender) {
        return new AlertEventSender(alertsService, snmpSenderFactory, emailSender, ClientType.mc);
    }

    @Bean
    public AlertEventSender scAlertEventSender(AlertsService alertsService, SnmpSenderFactory snmpSenderFactory,
                                               EmailSender emailSender) {
        return new AlertEventSender(alertsService, snmpSenderFactory, emailSender, ClientType.sc);
    }
}
