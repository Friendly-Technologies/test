package com.friendly.services.settings.alerts;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.settings.AlertEvent;
import com.friendly.commons.models.settings.AlertEventType;
import com.friendly.commons.models.settings.AlertTimesType;
import com.friendly.commons.models.settings.Alerts;
import com.friendly.commons.models.settings.AlertsResponse;
import com.friendly.commons.models.settings.acs.AcsLicense;
import com.friendly.commons.models.settings.request.EmailsRequest;
import com.friendly.commons.models.settings.response.AlertEventsResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.alerts.orm.acs.model.AcsInfoEntity;
import com.friendly.services.settings.alerts.orm.iotw.model.AlertsEntity;
import com.friendly.services.settings.alerts.orm.iotw.model.AlertsSpecificDomainEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.alerts.orm.acs.repository.AcsInfoRepository;
import com.friendly.services.settings.alerts.orm.acs.repository.AcsMonitoringDataRepository;
import com.friendly.services.settings.alerts.orm.iotw.repository.AlertsRepository;
import com.friendly.services.settings.alerts.orm.iotw.repository.AlertsSpecificDomainRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.acs.AcsLicenseService;
import com.friendly.services.settings.acs.LicenceRepositoryHealthCheck;
import com.friendly.services.settings.alerts.mapper.AlertsMapper;
import com.friendly.services.settings.alerts.sender.AlertEventSender;
import com.friendly.services.settings.alerts.sender.AlertEventSenderFactory;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import com.ftacs.ACSWebService;
import com.ftacs.Exception_Exception;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.UserActivityType.CONFIGURING_ALERTS;
import static com.friendly.commons.models.settings.AlertEventType.ACS_CONNECTION;
import static com.friendly.commons.models.settings.AlertEventType.DB_CONNECTION;
import static com.friendly.commons.models.settings.AlertEventType.DENIED_ACCESS_LWM2M;
import static com.friendly.commons.models.settings.AlertEventType.DENIED_ACCESS_MQTT;
import static com.friendly.commons.models.settings.AlertEventType.DENIED_ACCESS_TR069;
import static com.friendly.commons.models.settings.AlertEventType.DENIED_ACCESS_USP;
import static com.friendly.commons.models.settings.AlertEventType.DIFFER_ACS_TIME;
import static com.friendly.commons.models.settings.AlertEventType.LICENCE_EXPIRE;
import static com.friendly.commons.models.settings.AlertEventType.LICENCE_HAS_EXPIRED;
import static com.friendly.commons.models.settings.AlertEventType.LIMIT_ALL_DEVICES;
import static com.friendly.commons.models.settings.AlertEventType.LIMIT_LWM2M;
import static com.friendly.commons.models.settings.AlertEventType.LIMIT_MQTT;
import static com.friendly.commons.models.settings.AlertEventType.LIMIT_TR069;
import static com.friendly.commons.models.settings.AlertEventType.LIMIT_USP;
import static com.friendly.commons.models.settings.AlertEventType.USED_90_LIMIT_ALL_DEVICES;
import static com.friendly.commons.models.settings.ProblemLevelType.HIGH;
import static com.friendly.commons.models.settings.ProblemLevelType.LOW;
import static com.friendly.commons.models.settings.ProblemLevelType.MEDIUM;
import static com.friendly.commons.models.settings.ProblemLevelType.NONE;
import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.commons.models.websocket.SettingType.ALERTS;

/**
 * Service that exposes the base functionality for interacting with {@link Alerts} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
public class AlertsService {
    private final UserRepository userRepository;

    @NonNull
    private final AlertsRepository alertsRepository;

    @NonNull AlertsSpecificDomainRepository alertsSpecificDomainRepository;

    @NonNull
    private final LicenceRepositoryHealthCheck licenceRepositoryHealthCheck;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final AlertsMapper alertsMapper;

    @NonNull
    private final WsSender wsSender;

    @NonNull
    private final UserService userService;

    @NonNull
    private final StatisticService statisticService;

    @NonNull
    private final AcsLicenseService acsLicenseService;
    @NonNull
    private final AcsInfoRepository acsInfoRepository;

    @NonNull
    private final AcsMonitoringDataRepository acsMonitoringDataRepository;
    @Lazy
    private final AlertEventSenderFactory senderFactory;

    public final AcsProvider acsProvider;

    public AlertsService(@NonNull final AlertsRepository alertsRepository,
                         @NonNull final AlertsSpecificDomainRepository alertsSpecificDomainRepository,
                         @NonNull LicenceRepositoryHealthCheck licenceRepositoryHealthCheck,
                         @NonNull final JwtService jwtService,
                         @NonNull final AlertsMapper alertsMapper,
                         @NonNull final WsSender wsSender,
                         @NonNull final UserService userService,
                         @NonNull final StatisticService statisticService,
                         @NonNull final AcsLicenseService acsLicenseService,
                         @Lazy final AlertEventSenderFactory senderFactory, AcsProvider acsProvider,
                         UserRepository userRepository, @NonNull AlertProvider alertProvider, @NonNull AcsInfoRepository acsInfoRepository, @NonNull AcsMonitoringDataRepository acsMonitoringDataRepository) {
        this.alertsRepository = alertsRepository;
        this.alertsSpecificDomainRepository = alertsSpecificDomainRepository;
        this.licenceRepositoryHealthCheck = licenceRepositoryHealthCheck;
        this.userService = userService;
        this.jwtService = jwtService;
        this.alertsMapper = alertsMapper;
        this.wsSender = wsSender;
        this.statisticService = statisticService;
        this.acsLicenseService = acsLicenseService;
        this.senderFactory = senderFactory;
        this.acsProvider = acsProvider;
        this.userRepository = userRepository;
        this.acsInfoRepository = acsInfoRepository;
        this.acsMonitoringDataRepository = acsMonitoringDataRepository;
    }

    /**
     * Get Alerts Setting
     *
     * @return {@link Alerts} setting
     */
    public AlertsResponse getAlertsSetting(final String token) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final Optional<UserEntity> user = userRepository.findById(userId);
        Integer domainId = user.get().getDomainId();

        Alerts defaultAlerts = getAlertsSetting(clientType);
        Alerts specificDomainAlerts =
                getAlertsSettingForClientTypeAndDomainId(clientType, domainId);
        return new AlertsResponse(defaultAlerts, specificDomainAlerts);
    }

    private Alerts getAlertsSettingForClientTypeAndDomainId(ClientType clientType, Integer domainId) {
        return alertsSpecificDomainRepository.findByClientTypeAndDomainId(clientType, domainId)
                .map(alertsMapper::alertsSpecificEntityToAlerts)
                .orElse( null);

    }

    public AlertEventsResponse getAlertEvents(final String token, final boolean isWebServiceError) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());

        return new AlertEventsResponse(getAlertEvents(clientType, user.getDateFormat(), isWebServiceError));
    }

    public NodesListResponse getAcsNodesInfo(final String token) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        ACSWebService acsWebService = AcsProvider.getAcsWebService(clientType);
        return getAcsNodesInfo(acsWebService);
    }

    public NodesListResponse getAcsNodesInfo(final ACSWebService acsWebService) {
        int nodeNum = 1;
        List<NodeResponse> items = new ArrayList<>();
        boolean isActiveNode;
        long statisticsInterval;
        try {
            statisticsInterval = (Long.parseLong(acsWebService.getACSParam("statisticsEnable")));
        } catch (Exception_Exception e) {
            throw new RuntimeException(e);
        }

        List<AcsInfoEntity> nodesEntities = acsInfoRepository.findAll().stream()
                .sorted(Comparator.comparing(AcsInfoEntity::getStartDate))
                .collect(Collectors.toList());
        for (AcsInfoEntity node : nodesEntities) {
            isActiveNode = acsMonitoringDataRepository.findById(node.getNodeName()).get().getCreated().plusSeconds(statisticsInterval + 5).getEpochSecond() * 1000 > System.currentTimeMillis();
            items.add(NodeResponse.builder()
                    .name("Node " + nodeNum)
                    .nodeName(node.getNodeName())
                    .state(isActiveNode ? node.getAcsVersion() + " UP" : "DOWN")
                    .build());
            nodeNum++;
        }
        return new NodesListResponse(items);
    }

    private long diffTimeBetweenServers(ACSWebService acsWebService) {
            GregorianCalendar gregorianCalendar = acsWebService.getServerDate().getDate().toGregorianCalendar();
            LocalDateTime acsTime = gregorianCalendar.toZonedDateTime().toLocalDateTime();
            LocalDateTime localTime = LocalDateTime.now();
            return ChronoUnit.SECONDS.between(acsTime, localTime);
    }

    public List<AlertEvent> getAlertEvents(final ClientType clientType, final String dateFormat, final boolean isWebServiceError) {
        final Map<AlertEventType, AlertEvent> alertMap = new LinkedHashMap<>(AlertProvider.getAlertMap());

        ACSWebService acsWebService = AcsProvider.getAcsWebService(clientType);

        if (isWebServiceError || AlertProvider.getAcsIsDown(clientType)) {
            alertMap.get(ACS_CONNECTION).setProblemLevel(MEDIUM);
        } else {
            final long diffTimeBetweenServers = diffTimeBetweenServers(acsWebService);
            if (diffTimeBetweenServers > 15) {
                AlertEvent alertEvent = alertMap.get(DIFFER_ACS_TIME);
                final String description = alertEvent.getDescription()
                        .replace("{-}", Long.toString(diffTimeBetweenServers));
                alertEvent.setDescription(description);
                alertEvent.setProblemLevel(HIGH);
            }

//            final List<NodeResponse> nodes = getAcsNodesInfo(acsWebService).getItems();
//            boolean isAnyDownState = nodes.stream().map(NodeResponse::getState).anyMatch(state -> state.equals("DOWN"));
//            if (nodes.isEmpty()) {
//                alertMap.get(ACS_CONNECTION).setProblemLevel(HIGH);
//            }
//            if (isAnyDownState) {
//                int nodesCount = nodes.size();
//                int activeNodes = (int) nodes.stream().filter(n -> n.getState().contains("UP")).count();
//                int notActiveNodes = nodesCount - activeNodes;
//                int notActiveNodesPercent = notActiveNodes * 100 / nodesCount;
//                if (notActiveNodesPercent < 30) {
//                    alertMap.get(ACS_CONNECTION).setProblemLevel(LOW);
//                } else if (notActiveNodesPercent < 50) {
//                    alertMap.get(ACS_CONNECTION).setProblemLevel(MEDIUM);
//                } else {
//                    alertMap.get(ACS_CONNECTION).setProblemLevel(HIGH);
//                }
//            }
        }


        if (!licenceRepositoryHealthCheck.isHealth()) {
            alertMap.get(DB_CONNECTION).setProblemLevel(HIGH);
        }

        if (alertMap.get(DB_CONNECTION).getProblemLevel().equals(NONE)) {
            final AcsLicense license = acsLicenseService.getLicense(clientType, dateFormat);
            if (license != null) {
                Instant timeExpiration = license.getExpireDateIso();
                if (timeExpiration != null) {
                    long diff = ChronoUnit.DAYS.between(Instant.now(), timeExpiration.plus(1, ChronoUnit.DAYS));
                    if (diff < 0) {
                        alertMap.get(LICENCE_HAS_EXPIRED).setProblemLevel(HIGH);
                    } else {
                        if (diff <= 14) {
                            String description = alertMap.get(LICENCE_EXPIRE).getDescription()
                                    .replace("{-}", Long.toString(diff));
                            alertMap.get(LICENCE_EXPIRE).setDescription(description);
                            alertMap.get(LICENCE_EXPIRE).setProblemLevel(MEDIUM);
                        } else if (diff <= 30) {
                            String description = alertMap.get(LICENCE_EXPIRE).getDescription()
                                    .replace("{-}", Long.toString(diff));
                            alertMap.get(LICENCE_EXPIRE).setDescription(description);
                            alertMap.get(LICENCE_EXPIRE).setProblemLevel(LOW);
                        }
                    }
                }
                boolean exceeded = setCpeLimitAlert(alertMap, license.getLimitDevices(), license.getUseDevices(), USED_90_LIMIT_ALL_DEVICES, LIMIT_ALL_DEVICES);
                if (!exceeded) {
                    setCpeLimitAlert(alertMap, license.getLimitTR069(), license.getUseTR069(), LIMIT_TR069, DENIED_ACCESS_TR069);
                    setCpeLimitAlert(alertMap, license.getLimitLWM2M(), license.getUseLWM2M(), LIMIT_LWM2M, DENIED_ACCESS_LWM2M);
                    setCpeLimitAlert(alertMap, license.getLimitMQTT(), license.getUseMQTT(), LIMIT_MQTT, DENIED_ACCESS_MQTT);
                    setCpeLimitAlert(alertMap, license.getLimitUSP(), license.getUseUSP(), LIMIT_USP, DENIED_ACCESS_USP);
                }
            }
        }

        return new ArrayList<>(alertMap.values());
    }

    public boolean setCpeLimitAlert(final Map<AlertEventType, AlertEvent> alertMap,
                                 final String limit, final Long use,
                                 final AlertEventType eventLimitType, final AlertEventType eventDeniedType) {
        boolean result = false;
        if (limit != null && !limit.equals("unlimited") && !limit.equals("locked")) {
            final long percent = limit.equals("0") ? 0 : (use * 100 / Integer.parseInt(limit));
            if (percent >= 100) {
                alertMap.get(eventDeniedType).setProblemLevel(HIGH);
                return true;
            } else if (percent >= 90) {
                // {-} not working since we have 90 hardcoded in description, to think about it
                final String description = alertMap.get(eventLimitType).getDescription()
                        .replace("{-}", Long.toString(percent));
                alertMap.get(eventLimitType).setDescription(description);
                alertMap.get(eventLimitType).setProblemLevel(MEDIUM);
            } else if (percent >= 75) {
                final String description = alertMap.get(eventLimitType).getDescription()
                        .replace("{-}", Long.toString(percent));
                alertMap.get(eventLimitType).setDescription(description);
                alertMap.get(eventLimitType).setProblemLevel(LOW);
            }
        }
        return result;
    }

    /**
     * Update Alerts Setting
     *
     * @return {@link Alerts} setting
     */
    @Transactional
    public Alerts updateAlertsSetting(final String token, final Alerts alerts) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final Optional<UserEntity> user = userRepository.findById(userId);
        Integer domainId = user.get().getDomainId();

        Alerts result = null;

        if(domainId == 0) {
            final AlertsEntity alertsEntity = alertsRepository.saveAndFlush(
                    alertsMapper.alertsToAlertsEntity(clientType, alerts));
            result = alertsMapper.alertsEntityToAlerts(alertsEntity);
        } else {

            Optional<AlertsSpecificDomainEntity> optional
                    = alertsSpecificDomainRepository.findByClientTypeAndDomainId(clientType, domainId);
            if(optional.isPresent()) {
                Alerts oldAlerts = fromEntity(optional.get());
                if(isNull(alerts)) {
                    alertsSpecificDomainRepository.delete(optional.get());
                    return new Alerts();
                }
                if(alerts.equals(oldAlerts)) {
                    return alerts;
                }
                alertsSpecificDomainRepository.save(alertsMapper.toSpecificEntity(
                        optional.get().getId(), domainId, clientType, alerts
                ));
            } else {
                AlertsSpecificDomainEntity newEntity
                        = alertsMapper.toSpecificEntity(null, domainId, clientType, alerts);
                AlertsSpecificDomainEntity alertsEntity
                        = alertsSpecificDomainRepository.saveAndFlush(newEntity);
                result = fromEntity(alertsEntity);
            }
        }

        if(result == null) {
            return new Alerts();
        }

        final AlertEventSender alertEventSender = senderFactory.getAlertEventSender(clientType);

        if (result.getAlertTimesType() == AlertTimesType.INTERVAL) {
            alertEventSender.startScheduleInterval(result);
        } else {
            alertEventSender.startOnce(result, clientType);
        }

        statisticService.addUserLogAct(UserActivityLog.builder()
                .userId(session.getUserId())
                .clientType(clientType)
                .activityType(CONFIGURING_ALERTS)
                .note("Times type=" + result.getAlertTimesType() +
                        "; Interval=" + result.getInterval() +
                        "; ViaProgram=" + result.isViaProgram() +
                        "; ViaEmail=" + result.isViaEmail() +
                        "; ViaSms=" + result.isViaSms() +
                        "; ViaSnmp=" + result.isViaSnmp())
                .build());
        wsSender.sendSettingEvent(clientType, UPDATE, ALERTS, result);
        return result;
    }

    private Alerts fromEntity(AlertsSpecificDomainEntity e) {
        if(e == null) {
            return null;
        }
        return Alerts.builder()
                .alertTimesType(e.getAlertTimesType())
                .interval(e.getInterval())
                .emails(e.getEmails())
                .phoneNumbers(e.getPhoneNumbers())
                .viaEmail(e.isViaEmail())
                .viaSms(e.isViaSms())
                .viaProgram(e.isViaProgram())
                .viaSnmp(e.isViaSnmp())
                .build();
    }

    private boolean isNull(Alerts alerts) {
        return alerts.getAlertTimesType() == null
                && alerts.getInterval() == null
                && alerts.getEmails() == null
                && alerts.getPhoneNumbers() == null
                && !alerts.isViaEmail()
                && !alerts.isViaSnmp()
                && !alerts.isViaSms()
                && !alerts.isViaProgram();
    }

    /**
     * Delete values to Alerts Setting
     *
     * @return {@link Alerts} setting
     */
    @Transactional
    public Alerts deleteValuesFromAlerts(final String token, final EmailsRequest request) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        AlertsEntity alertsEntity = alertsRepository.findById(clientType)
                .orElseThrow(() -> new RuntimeException("AlertsEntity does not exist"));

        Set<String> emails = alertsEntity.getEmails();
        Set<String> values = request.getEmails();
        emails.removeAll(values);
        alertsEntity.setEmails(emails);
        alertsRepository.save(alertsEntity);

        final Alerts result = getAlertsSetting(clientType);

        statisticService.addUserLogAct(UserActivityLog.builder()
                .userId(session.getUserId())
                .clientType(clientType)
                .activityType(CONFIGURING_ALERTS)
                .note("Delete emails" + ":" +
                        StringUtils.join(values, ", "))
                .build());
        wsSender.sendSettingEvent(clientType, UPDATE, ALERTS, result);
        return result;
    }

    public Alerts getAlertsSetting(final ClientType clientType) {
        return alertsRepository.findById(clientType)
                .map(alertsMapper::alertsEntityToAlerts)
                .orElse(null);
    }

}
