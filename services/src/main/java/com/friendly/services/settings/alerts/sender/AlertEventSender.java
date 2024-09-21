package com.friendly.services.settings.alerts.sender;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.AlertEvent;
import com.friendly.commons.models.settings.AlertEventType;
import com.friendly.commons.models.settings.Alerts;
import com.friendly.commons.models.settings.ProblemLevelType;
import com.friendly.services.infrastructure.utils.mail.EmailSender;
import com.friendly.services.settings.alerts.AlertsService;
import com.friendly.services.settings.snmpserver.sender.SnmpSenderFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AlertEventSender {
    public static final int DEFAULT_DOMAIN_ID = -1;
    private final AlertsService alertsService;
    private final SnmpSenderFactory snmpSenderFactory;
    private final EmailSender emailSender;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();

    private final static Set<AlertEventType> SENT_SET = new ConcurrentSkipListSet<>();

    private final ClientType clientType;

    public AlertEventSender(AlertsService alertsService, SnmpSenderFactory snmpSenderFactory,
                            EmailSender emailSender, ClientType clientType) {
        this.alertsService = alertsService;
        this.snmpSenderFactory = snmpSenderFactory;
        this.emailSender = emailSender;
        this.clientType = clientType;
    }

    public void startScheduleInterval(final Alerts alerts) {
        stop();
        scheduler.scheduleAtFixedRate(
                sendAlerts(alerts, alerts.getEmails(), clientType, true), 0, alerts.getInterval(), SECONDS);
    }


    public void startOnce(final Alerts alerts, ClientType clientType) {
        stop();
        singleExecutorService.execute(sendAlerts(alerts, alerts.getEmails(), clientType, false));
    }

    private void stop() {
        scheduler.shutdown();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    private Runnable sendAlerts(final Alerts alerts,
                                  final Set<String> emails,
                                  final ClientType clientType,
                                  final boolean isInterval) {
        return () -> {
            if (alerts.isViaEmail()) {
                sendEmail(emails, clientType, isInterval);
            }
            if (alerts.isViaSnmp() && clientType.equals(ClientType.mc)) {
                sendSnmpTrap(clientType, isInterval);
            }
        };
    }

    private void sendEmail(final Set<String> emails,
                           final ClientType clientType,
                           final boolean isInterval) {
        final String message = getMessage(clientType, isInterval);

        if (StringUtils.isNotBlank(message)) {
            emails.forEach(email -> emailSender.sendSimpleMessage(clientType, email, message));
        }
    }

    private void sendSnmpTrap(final ClientType clientType,
                              final boolean isInterval) {
        final String message = getMessage(clientType, isInterval);

        if (StringUtils.isNotBlank(message)) {
            snmpSenderFactory.getSnmpSender(ClientType.sc).sendSnmpTrap(message);
        }
    }

    private String getMessage(final ClientType clientType, final boolean isInterval) {
        return alertsService.getAlertEvents(clientType, null, false)
                .stream()
                .filter(e -> filter(e, isInterval))
                .map(e -> {
                    SENT_SET.add(e.getEventType());
                    return e.getDescription();
                })
                .collect(Collectors.joining("\n"));
    }

    private boolean filter(final AlertEvent event, final boolean isInterval) {
        return !event.getProblemLevel().equals(ProblemLevelType.NONE)
                && (isInterval || !SENT_SET.contains(event.getEventType()));
    }

}
