package com.friendly.services.infrastructure.utils.startup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.AlertTimesType;
import com.friendly.commons.models.settings.Alerts;
import com.friendly.commons.models.settings.ScheduledEvent;
import com.friendly.commons.models.settings.SnmpServer;
import com.friendly.services.settings.alerts.orm.iotw.model.AlertsEntity;
import com.friendly.services.settings.fileserver.orm.iotw.model.FileServerEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceItemEntity;
import com.friendly.services.settings.notification.orm.iotw.model.NotificationInfoEntity;
import com.friendly.services.uiservices.system.orm.iotw.model.ServerDetailsEntity;
import com.friendly.services.settings.snmpserver.orm.iotw.model.SnmpServerEntity;
import com.friendly.services.settings.alerts.orm.iotw.repository.AlertsRepository;
import com.friendly.services.settings.emailserver.orm.iotw.repository.EmailServerRepository;
import com.friendly.services.settings.fileserver.orm.iotw.repository.FileServerRepository;
import com.friendly.services.settings.userinterface.orm.iotw.repository.InterfaceItemRepository;
import com.friendly.services.settings.notification.orm.iotw.repository.NotificationInfoRepository;
import com.friendly.services.uiservices.system.orm.iotw.repository.ServerDetailsRepository;
import com.friendly.services.settings.snmpserver.orm.iotw.repository.SnmpServerRepository;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.settings.alerts.AlertsService;
import com.friendly.services.settings.alerts.sender.AlertEventSender;
import com.friendly.services.settings.alerts.sender.AlertEventSenderFactory;
import com.friendly.services.settings.usergroup.UserGroupService;
import com.friendly.services.settings.userinterface.InterfaceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.commons.models.settings.ScheduledEvent.COMPLETED;
import static com.friendly.commons.models.settings.ScheduledEvent.ERROR;
import static com.friendly.commons.models.settings.ScheduledEvent.OVERDUE;
import static com.friendly.commons.models.settings.ScheduledEvent.SOON_STARTED;
import static com.friendly.commons.models.settings.ScheduledEvent.STARTED;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("userInitializer")
public class SettingInitializer {
    private final NotificationInfoRepository notificationInfoRepository;

    @Value("${server.path}")
    private String appPath;

    @Value("${customization.path}")
    private String customizationPath;

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final FileServerRepository fileServerRepository;

    @NonNull
    private final ServerDetailsRepository serverDetailsRepository;

    @NonNull
    private final EmailServerRepository emailServerRepository;

    @NonNull
    private final AlertsService alertsService;

    @NonNull
    private final SnmpServerRepository snmpServerRepository;

    @NonNull
    private final AlertsRepository alertsRepository;

    @NonNull
    private final InterfaceItemRepository interfaceRepository;

    @NonNull
    private final InterfaceService interfaceService;

    @NonNull
    private final UserGroupService userGroupService;

    @NonNull
    private final AlertEventSenderFactory alertSenderFactory;

    @PostConstruct
    @Transactional
    public void init() {
        setInterfaceItemsFromFile();
        userGroupService.updateUserGroupTemplates(ClientType.sc);
        userGroupService.updateUserGroupTemplates(ClientType.mc);

        setFileServerSettings(ClientType.sc);
        addEmailServer(ClientType.sc);
        addAlerts(ClientType.sc);
        setConnectionCheckTime(ClientType.sc);
        startAlertsNotifications(ClientType.sc);
//        setSnmpServerFromFile(ClientType.sc);
        setUpNotificationInfo(ClientType.sc);

        setFileServerSettings(ClientType.mc);
        addEmailServer(ClientType.mc);
        addAlerts(ClientType.mc);
        setConnectionCheckTime(ClientType.mc);
        startAlertsNotifications(ClientType.mc);
//        setSnmpServerFromFile(ClientType.mc);
        setUpNotificationInfo(ClientType.mc);
    }

    private void setUpNotificationInfo(ClientType clientType) {
        if (!notificationInfoRepository.findById(clientType)
                .isPresent()) {

            List<ScheduledEvent> events = new ArrayList<>();
            List<String> emails = new ArrayList<>();
            List<String> phoneNumbers = new ArrayList<>();

            events.add(COMPLETED);
            events.add(ERROR);
            events.add(OVERDUE);
            events.add(SOON_STARTED);
            events.add(STARTED);

            emails.add("1");

            phoneNumbers.add("6");
            phoneNumbers.add("7");
            phoneNumbers.add("8");

            notificationInfoRepository.saveAndFlush(NotificationInfoEntity.builder()
                    .id(clientType)
                    .byEmail(true)
                    .emails(emails)
                    .bySms(false)
                    .phones(phoneNumbers)
                    .checkedEvents(events)
                    .sendEvents(true)
                    .sendMonitoring(true)
                    .sendUG(true)
                    .soonMinutes(5)
                    .subject("Notification")
                    .build());
        }
    }

    private void setInterfaceItemsFromFile() {
        try (FileReader reader = new FileReader(appPath + "interfaceItems.json")) {
            final List<InterfaceItemEntity> interfaceItems =
                    Arrays.asList(mapper.readValue(reader, InterfaceItemEntity[].class));

            List<InterfaceItemEntity> itemEntityList = interfaceItems.stream()
                    .filter(item -> !interfaceRepository.findById(item.getId()).isPresent())
                    .peek(p -> p.setInterfaceDescriptions(p.getInterfaceDescriptions().stream()
                            .peek(d -> d.setInterfaceDescriptionId(p.getId())).collect(Collectors.toList())))
                    .collect(Collectors.toList());
            interfaceRepository.saveAll(itemEntityList);
        } catch (IOException e) {
            log.error("interfaceItems.json not found");
        }
    }

    private void setSnmpServerFromFile(final ClientType clientType) {
        if (!snmpServerRepository.findById(clientType)
                .isPresent()) {
            try (FileReader reader = new FileReader(customizationPath + clientType + "/device/settings/snmpServer.json")) {
                final SnmpServer snmpServer = mapper.readValue(reader, SnmpServer.class);

                snmpServerRepository.saveAndFlush(SnmpServerEntity.builder()
                        .id(clientType)
                        .host(snmpServer.getHost())
                        .port(snmpServer.getPort())
                        .community(snmpServer.getCommunity())
                        .version(snmpServer.getVersion())
                        .build());
            } catch (IOException e) {
                log.error("snmpServer.json not found");
            }
        }
    }


    private void setFileServerSettings(final ClientType clientType) {
        if (fileServerRepository.getFileServerEntity(clientType, 0) == null) {
            final ServerDetailsEntity fileManagementFtpServerDetails =
                    serverDetailsRepository.saveAndFlush(ServerDetailsEntity.builder()
                            .name("DownloadFtp")
                            .build());
            final ServerDetailsEntity downloadHttpServerDetails =
                    serverDetailsRepository.saveAndFlush(ServerDetailsEntity.builder()
                            .name("DownloadHttp")
                            .build());
            final ServerDetailsEntity uploadFtpServerDetails =
                    serverDetailsRepository.saveAndFlush(ServerDetailsEntity.builder()
                            .name("UploadFtp")
                            .build());
            final ServerDetailsEntity uploadHttpServerDetails =
                    serverDetailsRepository.saveAndFlush(ServerDetailsEntity.builder()
                            .name("UploadHttp")
                            .build());

            fileServerRepository.saveAndFlush(FileServerEntity.builder()
                    .clientType(clientType)
                    .domainId(0)
                    .serverDetails(
                            Arrays.asList(
                                    fileManagementFtpServerDetails,
                                    downloadHttpServerDetails,
                                    uploadFtpServerDetails,
                                    uploadHttpServerDetails))
                    .build());
        }
    }

    private void addEmailServer(final ClientType clientType) {
        if (!emailServerRepository.findById(clientType).isPresent()) {
//            emailServerRepository.saveAndFlush(EmailServerEntity.builder()
//                    .id(clientType)
//                    .enableSSL(true)
//                    .from("Friendly")
//                    .host("smtp.gmail.com")
//                    .password("friendly-tech")
//                    .port("587")
//                    .subject("Test message")
//                    .username("friendly.tech.test@gmail.com")
//                    .domainId(0)
//                    .build());
            log.warn("Email server undefined");
        }
    }

    private void addAlerts(final ClientType clientType) {
        if (!alertsRepository.findById(clientType).isPresent()) {
            alertsRepository.saveAndFlush(AlertsEntity.builder()
                    .id(clientType)
                    .interval(0L)
                    .alertTimesType(AlertTimesType.ONCE)
                    .viaEmail(false)
                    .viaProgram(true)
                    .viaSms(false)
                    .viaSnmp(false)
                    .build());
        }
    }

    private void setConnectionCheckTime(final ClientType clientType) {
        final Integer intervalSec =
                interfaceService.getInterfaceValue(clientType, "ConnectionCheckIntervalSec")
                        .map(Integer::parseInt)
                        .orElse(30);
        AlertProvider.setCheckInterval(clientType, intervalSec);
    }

    private void startAlertsNotifications(final ClientType clientType) {
        final Alerts alerts = alertsService.getAlertsSetting(clientType);
        if (alerts != null) {
            final AlertEventSender alertEventSender = alertSenderFactory.getAlertEventSender(clientType);
            if (alerts.getAlertTimesType().equals(AlertTimesType.INTERVAL)) {
                alertEventSender.startScheduleInterval(alerts);
            } else {
                alertEventSender.startOnce(alerts, clientType);
            }
        }
    }
}
