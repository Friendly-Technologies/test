package com.friendly.services.settings.alerts;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.AlertEvent;
import com.friendly.commons.models.settings.AlertEventType;
import com.friendly.commons.models.settings.acs.AcsProperties;
import com.friendly.services.ServicesApplication;
import com.friendly.services.infrastructure.base.ACSWebServiceMock;
import com.friendly.services.infrastructure.config.AcsConfig;
import com.friendly.services.infrastructure.config.jpa.DBContextHolder;
import com.friendly.services.device.info.orm.acs.repository.DeviceRepository;
import com.friendly.services.settings.acs.orm.acs.repository.LicenceRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.ftacs.ACSWebService;
import com.ftacs.Exception_Exception;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.friendly.commons.models.settings.AlertEventType.*;
import static com.friendly.commons.models.settings.ProblemLevelType.*;

@Component
@RequiredArgsConstructor
public class AlertProvider {

    @NonNull
    private final LicenceRepository licenceRepository;

    @NonNull
    private final DeviceRepository deviceRepository;

    @NonNull
    private final TaskScheduler scheduler;

    private final static Map<AlertEventType, AlertEvent> alertMap = new LinkedHashMap<>();

    private final static AtomicInteger scInterval = new AtomicInteger(30);
    private final static AtomicInteger mcInterval = new AtomicInteger(30);

    private final static AtomicBoolean acsIsDownSc = new AtomicBoolean(false);
    private final static AtomicBoolean acsIsDownMc = new AtomicBoolean(false);

    private final static AtomicBoolean dbIsDownSc = new AtomicBoolean(false);
    private final static AtomicBoolean dbIsDownMc = new AtomicBoolean(false);

    public static void setCheckInterval(final ClientType clientType, final Integer interval) {
        if (clientType.equals(ClientType.sc)) {
            scInterval.set(interval);
        } else {
            mcInterval.set(interval);
        }
    }

    public static Integer getCheckInterval(final ClientType clientType) {
        return clientType.equals(ClientType.sc) ? scInterval.get() : mcInterval.get();
    }

    @PostConstruct
    private void init() {
        alertMap.clear();
        alertMap.put(ACS_CONNECTION, AlertEvent.builder()
                .eventType(ACS_CONNECTION)
                .problemLevel(NONE)
                .description("Communication problem with ACS")
                .build());

        alertMap.put(DB_CONNECTION, AlertEvent.builder()
                .eventType(DB_CONNECTION)
                .problemLevel(NONE)
                .description("DB server stopped")
                .build());

        alertMap.put(LICENCE_EXPIRE, AlertEvent.builder()
                .eventType(LICENCE_EXPIRE)
                .problemLevel(NONE)
                .description("Your license will expire in {-} days")
                .build());

        alertMap.put(LICENCE_HAS_EXPIRED, AlertEvent.builder()
                .eventType(LICENCE_HAS_EXPIRED)
                .problemLevel(NONE)
                .description("Your license has expired")
                .build());

        alertMap.put(DIFFER_ACS_TIME, AlertEvent.builder()
                .eventType(DIFFER_ACS_TIME)
                .problemLevel(NONE)
                .description("Servers time differ by more than {-} seconds")
                .build());

        alertMap.put(DENIED_ACCESS_ALL_DEVICES, AlertEvent.builder()
                .eventType(DENIED_ACCESS_ALL_DEVICES)
                .problemLevel(NONE)
                .description("Total devices: access is denied for all devices")
                .build());

        alertMap.put(USED_90_LIMIT_ALL_DEVICES, AlertEvent.builder()
                .eventType(USED_90_LIMIT_ALL_DEVICES)
                .problemLevel(NONE)
                .description("Total devices: 90% of the device limit is used")
                .build());

        alertMap.put(LIMIT_ALL_DEVICES, AlertEvent.builder()
                .eventType(LIMIT_ALL_DEVICES)
                .problemLevel(NONE)
                .description("Total devices: device limit exceeded")
                .build());

        alertMap.put(LIMIT_TR069, AlertEvent.builder()
                .eventType(LIMIT_TR069)
                .problemLevel(NONE)
                .description("TR069 devices: 90% of the device limit is used")
                .build());

        alertMap.put(DENIED_ACCESS_TR069, AlertEvent.builder()
                .eventType(DENIED_ACCESS_TR069)
                .problemLevel(NONE)
                .description("TR069 devices: device limit exceeded")
                .build());

        alertMap.put(LIMIT_LWM2M, AlertEvent.builder()
                .eventType(LIMIT_LWM2M)
                .problemLevel(NONE)
                .description("LWM2M devices: 90% of the device limit is used")
                .build());

        alertMap.put(DENIED_ACCESS_LWM2M, AlertEvent.builder()
                .eventType(DENIED_ACCESS_LWM2M)
                .problemLevel(NONE)
                .description("LWM2M devices: device limit exceeded")
                .build());

        alertMap.put(LIMIT_MQTT, AlertEvent.builder()
                .eventType(LIMIT_MQTT)
                .problemLevel(NONE)
                .description("MQTT devices: 90% of the device limit is used")
                .build());

        alertMap.put(DENIED_ACCESS_MQTT, AlertEvent.builder()
                .eventType(DENIED_ACCESS_MQTT)
                .problemLevel(NONE)
                .description("MQTT devices: device limit exceeded")
                .build());

        alertMap.put(LIMIT_USP, AlertEvent.builder()
                .eventType(LIMIT_USP)
                .problemLevel(NONE)
                .description("USP devices: 90% of the device limit is used")
                .build());

        alertMap.put(DENIED_ACCESS_USP, AlertEvent.builder()
                .eventType(DENIED_ACCESS_USP)
                .problemLevel(NONE)
                .description("USP devices: device limit exceeded")
                .build());
    }

    public static Map<AlertEventType, AlertEvent> getAlertMap() {
        clearSensitiveAlertsProblemLevel();
        return alertMap;
    }

    public static void clearSensitiveAlertsProblemLevel() {
        alertMap.get(DIFFER_ACS_TIME).setProblemLevel(NONE);
        alertMap.get(ACS_CONNECTION).setProblemLevel(NONE);
        alertMap.get(DB_CONNECTION).setProblemLevel(NONE);
        alertMap.get(LICENCE_HAS_EXPIRED).setProblemLevel(NONE);
        alertMap.get(LICENCE_EXPIRE).setProblemLevel(NONE);
        alertMap.get(USED_90_LIMIT_ALL_DEVICES).setProblemLevel(NONE);
        alertMap.get(LIMIT_ALL_DEVICES).setProblemLevel(NONE);
        alertMap.get(LIMIT_TR069).setProblemLevel(NONE);
        alertMap.get(DENIED_ACCESS_TR069).setProblemLevel(NONE);
        alertMap.get(LIMIT_LWM2M).setProblemLevel(NONE);
        alertMap.get(DENIED_ACCESS_LWM2M).setProblemLevel(NONE);
        alertMap.get(LIMIT_MQTT).setProblemLevel(NONE);
        alertMap.get(DENIED_ACCESS_MQTT).setProblemLevel(NONE);
        alertMap.get(LIMIT_USP).setProblemLevel(NONE);
        alertMap.get(DENIED_ACCESS_USP).setProblemLevel(NONE);
    }

    public static boolean getAcsIsDown(final ClientType clientType) {
        return clientType.equals(ClientType.sc) ? acsIsDownSc.get() : acsIsDownMc.get();
    }

    public void setAcsIsDown() {
        scheduler.scheduleWithFixedDelay(checkAcs(ClientType.sc), scInterval.get() * 1000);
        scheduler.scheduleWithFixedDelay(checkAcs(ClientType.mc), mcInterval.get() * 1000);
    }

    public static boolean getDbIsDown(final ClientType clientType) {
        return clientType.equals(ClientType.sc) ? dbIsDownSc.get() : dbIsDownMc.get();
    }

    public void setDbIsDown() {
        scheduler.scheduleWithFixedDelay(checkDb(ClientType.sc), scInterval.get() * 1000);
        scheduler.scheduleWithFixedDelay(checkDb(ClientType.mc), mcInterval.get() * 1000);
    }

    private Runnable checkAcs(final ClientType clientType) {
        return () -> {
            if (clientType.equals(ClientType.sc) ? acsIsDownSc.get() : acsIsDownMc.get()) {
                final ACSWebService acsWebService = AcsProvider.getAcsWebService(clientType);
                if (acsWebService instanceof ACSWebServiceMock) {
                    final AcsProperties acsProperties = AcsConfig.getAcsProperties();
                    try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
                        final HttpGet httpGet = new HttpGet(
                                "http://" + acsProperties.getHost() + ":" + acsProperties.getPort() +
                                        "/ACSServer-ACS/ACSWebService?wsdl");
                        final String encoding = DatatypeConverter.printBase64Binary(
                                (acsProperties.getLogin() + ":" + acsProperties.getPort())
                                        .getBytes(StandardCharsets.UTF_8));
                        httpGet.setHeader("Authorization", "Basic " + encoding);
                        final HttpResponse response = client.execute(httpGet);
                        final boolean isDown = response.getStatusLine().getStatusCode() != 200;

                        setAcsIsDown(clientType, isDown);
                        if (!isDown) {
                            ServicesApplication.restart();
                        }
                    } catch (IOException e) {
                        setAcsIsDown(clientType, true);
                    }
                } else {
                    try {
                        setAcsIsDown(clientType, isAcsDown(acsWebService));
                    } catch (WebServiceException e) {
                        setAcsIsDown(clientType, true);
                    }
                }
            }
        };
    }

    private boolean isAcsDown(final ACSWebService acsWebService) {
        try {
            return acsWebService.getServerDate().getDate() == null
                    || acsWebService.getACSParam("statisticsEnable") == null
                    || acsWebService.getACSParam("statisticsEnable").equals("---")
                    || !isValidStatisticsData(acsWebService);
        } catch (Exception_Exception e) {
            return true;
        }
    }

    private boolean isValidStatisticsData(final ACSWebService acsWebService) {
        try {
            int statisticsInterval = Integer.parseInt(acsWebService.getACSParam("statisticsEnable"));
            if (statisticsInterval < 2) {
                return true;
            } else {
                Timestamp latestAcsActivity = deviceRepository.getAcsMonitoringCreatedMls();
                return (latestAcsActivity.getTime() + statisticsInterval * 1000L) >= System.currentTimeMillis();
            }
        } catch (Exception_Exception e) {
            return true;
        }
    }

    private static void setAcsIsDown(final ClientType clientType, final boolean isDown) {
        if (clientType.equals(ClientType.sc)) {
            acsIsDownSc.set(isDown);
        } else {
            acsIsDownMc.set(isDown);
        }
    }

    private Runnable checkDb(final ClientType clientType) {
        return () -> {
            if (clientType.equals(ClientType.sc) ? dbIsDownSc.get() : dbIsDownMc.get()) {
                DBContextHolder.setCurrentDb(clientType);
                try {
                    licenceRepository.findAll();
                    if (clientType.equals(ClientType.sc)) {
                        acsIsDownSc.set(false);
                    } else {
                        acsIsDownMc.set(false);
                    }
                } catch (JDBCConnectionException | DataAccessResourceFailureException |
                         InvalidDataAccessResourceUsageException e) {
                    //ignored
                }
            }
        };
    }

}
