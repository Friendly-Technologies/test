package com.friendly.services.qoemonitoring.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.exceptions.FriendlyPermissionException;
import com.friendly.commons.models.device.CpeData;
import com.friendly.commons.models.device.DiagIpPing;
import com.friendly.commons.models.device.NetworkMap;
import com.friendly.commons.models.device.RssiHost;
import com.friendly.commons.models.device.UserExperienceBody;
import com.friendly.commons.models.device.UserExperienceConditionFilter;
import com.friendly.commons.models.device.UserExperienceConditionType;
import com.friendly.commons.models.device.WifiEvent;
import com.friendly.commons.models.device.frame.ConditionFilter;
import com.friendly.commons.models.device.frame.ConditionType;
import com.friendly.commons.models.device.frame.KpiData;
import com.friendly.commons.models.device.frame.SpeedTest;
import com.friendly.commons.models.device.frame.response.GetQoeDetailsResponse;
import com.friendly.commons.models.device.frame.response.GetSpeedTestResponse;
import com.friendly.commons.models.device.response.AssociatedHosts;
import com.friendly.commons.models.device.response.RssiHostsResponse;
import com.friendly.commons.models.device.response.UserExperiencePing;
import com.friendly.commons.models.device.response.UserExperienceWifiEvents;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.user.Session;
import com.friendly.services.device.info.orm.acs.model.CpeEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.qoemonitoring.mapper.QoeDeviceMapper;
import com.friendly.services.qoemonitoring.mapper.QoeFrameMapper;
import com.friendly.services.qoemonitoring.orm.acs.repository.KpiRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagIpPingEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagSpeedTestEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.KpiDataEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.WifiCollisionEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.projections.UserExpAssocDeviceProjection;
import com.friendly.services.qoemonitoring.orm.qoe.model.projections.UserExpHostProjection;
import com.friendly.services.qoemonitoring.orm.qoe.repository.DiagIpPingRepository;
import com.friendly.services.qoemonitoring.orm.qoe.repository.DiagSpeedRepository;
import com.friendly.services.qoemonitoring.orm.qoe.repository.KpiDataRepository;
import com.friendly.services.qoemonitoring.orm.qoe.repository.UserExpAssocDeviceEntityRepository;
import com.friendly.services.qoemonitoring.orm.qoe.repository.UserExpHostRepository;
import com.friendly.services.qoemonitoring.orm.qoe.repository.WifiCollisionsRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DATABASE_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DEVICE_NOT_FOUND;
import static com.friendly.services.device.parameterstree.utils.ParameterUtil.getNode;
import static com.friendly.services.qoemonitoring.orm.qoe.model.enums.DiagnosticType.DownloadDiagnostic;
import static com.friendly.services.qoemonitoring.orm.qoe.model.enums.DiagnosticType.UploadDiagnostic;

@Slf4j
@RequiredArgsConstructor
@Service
public class QoeDeviceService {
    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final CpeRepository cpeRepository;
    private final QoeFrameMapper qoeFrameMapper;
    private final QoeFrameService qoeFrameService;
    @NonNull
    private final WifiCollisionsRepository wifiCollisionsRepository;

    @NonNull
    private final KpiRepository kpiRepository;

    @NonNull
    private final KpiDataRepository kpiDataRepository;

    @NonNull
    private final DiagIpPingRepository diagIpPingRepository;

    @NonNull
    private final DiagSpeedRepository diagSpeedRepository;

    @NonNull
    private final UserExpHostRepository userExpHostRepository;

    @NonNull
    private final UserExpAssocDeviceEntityRepository userExpAssocDeviceEntityRepository;

    @NonNull
    private final QoeDeviceMapper deviceMapper;

    @NonNull
    private final ParameterService parameterService;

    private UserExperienceUtilFilterService userExperienceUtilFilterService;



    public GetQoeDetailsResponse getUserExperienceCpuAndMemoryFilters(
            final String token, final UserExperienceBody request, final Boolean isMemory) {
        Session session = jwtService.getSession(token);

        Optional<CpeEntity> cpe = cpeRepository.findById(request.getDeviceId());
        if (!cpe.isPresent()) {
            throw new FriendlyIllegalArgumentException(DEVICE_NOT_FOUND);
        }
        String serial = cpe.get().getSerial();

        String rootNode =
                getNode(parameterService.findDevicePropertiesNameValuesMap(request.getDeviceId()));
        String kpiName = getKpiName(rootNode, isMemory);
        Long kpiId = kpiRepository.findByKpiName(kpiName).getId();

        Specification<KpiDataEntity> specification =
                qoeFrameService.getListFilers(
                        Arrays.asList(
                                ConditionFilter.builder()
                                        .compare(ConditionType.Equal)
                                        .conditionString(String.valueOf(serial))
                                        .build(),
                                ConditionFilter.builder()
                                        .compare(ConditionType.Equal)
                                        .conditionString(String.valueOf(kpiId))
                                        .build(),
                                deviceMapper.userExperienceConditionToConditionType(request.getConditions())),
                        session.getClientType(),
                        session.getZoneId());
        try {
            return new GetQoeDetailsResponse(
                    kpiDataRepository.findAll(specification).stream()
                            .map((KpiDataEntity entity)
                                    -> qoeFrameMapper.kpiDataEntityToObject(entity, session.getClientType(), session.getZoneId()))
                            .sorted(Comparator.comparing(KpiData::getCreatedIso))
                            .collect(Collectors.toList()));
        } catch (CannotCreateTransactionException ex) {
            throw new FriendlyPermissionException(DATABASE_NOT_FOUND, "clickhouse");
        }
    }

    private String getKpiName(String rootNode, boolean isMemory) {
        if (isMemory) {
            return rootNode.equals("Device.")
                    ? "Device.DeviceInfo.MemoryStatus.Free"
                    : "InternetGatewayDevice.DeviceInfo.MemoryStatus.Free";
        } else {
            return rootNode.equals("Device.")
                    ? "Device.DeviceInfo.ProcessStatus.CPUUsage"
                    : "InternetGatewayDevice.DeviceInfo.ProcessStatus.CPUUsage";
        }
    }

    public UserExperienceWifiEvents getUserExperienceWifiEvents(
            String token, UserExperienceBody request) {
        Session session = jwtService.getSession(token);

        Optional<CpeEntity> cpe = cpeRepository.findById(request.getDeviceId());
        if (!cpe.isPresent()) {
            throw new FriendlyIllegalArgumentException(DEVICE_NOT_FOUND);
        }
        String serial = cpe.get().getSerial();

        Specification<WifiCollisionEntity> specification =
                userExperienceUtilFilterService.getListFilters(
                        getConditionsForExperience(request.getConditions(), serial),
                        session.getClientType(),
                        session.getZoneId());
        try {
            return new UserExperienceWifiEvents(
                    wifiCollisionsRepository.findAll(specification).stream()
                            .filter(e -> e.getValue() != null)
                            .map(
                                    (WifiCollisionEntity entity) ->
                                            deviceMapper.userExperienceWifiEventFromEntity(
                                                    entity, session.getClientType(), session.getZoneId()))
                            .sorted(Comparator.comparing(WifiEvent::getCreatedIso))
                            .collect(Collectors.toList()));
        } catch (CannotCreateTransactionException ex) {
            throw new FriendlyPermissionException(DATABASE_NOT_FOUND, "clickhouse");
        }
    }

    public AssociatedHosts getUserExperienceAssociatedHosts(
            final String token, final LongIdRequest request) {
        final Session session = jwtService.getSession(token);

        final Optional<CpeEntity> cpe = cpeRepository.findById(request.getId());
        if (!cpe.isPresent()) {
            throw new FriendlyIllegalArgumentException(DEVICE_NOT_FOUND);
        }
        final String serial = cpe.get().getSerial();

        final Instant yesterday =
                DateTimeUtils.clientToServer(
                        Instant.now().minus(1, ChronoUnit.DAYS), session.getClientType(), session.getZoneId());

        List<NetworkMap> hosts = getHosts(serial, yesterday);

        if (hosts.isEmpty()) {
            return new AssociatedHosts();
        }

        final Map<Instant, Integer> wifiMap = new HashMap<>();
        final Map<Instant, Integer> lanMap = new HashMap<>();


        for (NetworkMap item : hosts) {
            if (item.getActive() != null
                    && !item.getActive().isEmpty()
                    && !item.getActive().equals("true")
                    && !item.getActive().equals("1")) {
                continue;
            }

            if (isWifi(item)) {
                incrementMap(wifiMap, item);
            } else {
                incrementMap(lanMap, item);
            }
        }

        Map<Instant, Integer> sortedMap = new TreeMap<>(lanMap);

        return new AssociatedHosts(mapToWifiLanList(wifiMap, session), mapToWifiLanList(sortedMap, session));
    }

    public UserExperiencePing getUserExperiencePingFilters(String token, UserExperienceBody request) {
        Session session = jwtService.getSession(token);

        Optional<CpeEntity> cpe = cpeRepository.findById(request.getDeviceId());
        if (!cpe.isPresent()) {
            throw new FriendlyIllegalArgumentException(DEVICE_NOT_FOUND);
        }
        String serial = cpe.get().getSerial();

        Specification<DiagIpPingEntity> specification =
                userExperienceUtilFilterService.getListFilters(
                        getConditionsForExperience(request.getConditions(), serial),
                        session.getClientType(),
                        session.getZoneId());
        try {
            return new UserExperiencePing(
                    diagIpPingRepository.findAll(specification).stream()
                            .filter(e -> e.getValue() != null)
                            .map(entity
                                    -> deviceMapper.userExperiencePingFromEntity(entity, session.getClientType(), session.getZoneId()))
                            .sorted(Comparator.comparing(DiagIpPing::getCreatedIso))
                            .collect(Collectors.toList()));
        } catch (CannotCreateTransactionException ex) {
            throw new FriendlyPermissionException(DATABASE_NOT_FOUND, "clickhouse");
        }
    }

    private List<UserExperienceConditionFilter> getConditionsForExperience(
            UserExperienceConditionFilter conditions, final String serial) {
        return Arrays.asList(
                UserExperienceConditionFilter.builder()
                        .compare(UserExperienceConditionType.Equal)
                        .conditionString(String.valueOf(serial))
                        .build(),
                conditions);
    }

    private void incrementMap(final Map<Instant, Integer> map, final NetworkMap item) {
        if (map.containsKey(item.getCreated())) {
            int cnt = map.get(item.getCreated());
            map.put(item.getCreated(), ++cnt);
        } else {
            map.put(item.getCreated(), 1);
        }
    }


    private List<NetworkMap> getHosts(
            final String serial, final Instant dateFrom) {

        List<UserExpHostProjection> entities =  userExpHostRepository.findAllBySerialAndCreated(serial, dateFrom);
        return entities.stream()
                .map(deviceMapper::cpeDataFromUserExp)
                .sorted(Comparator.comparing(NetworkMap::getName))
                .collect(Collectors.toList());
    }

    private List<CpeData> getCpeData(
            final String serial, final Instant dateFrom) {

        List<UserExpAssocDeviceProjection> entities = userExpAssocDeviceEntityRepository.findAllBySerialAndCreated(serial, dateFrom);
        return entities.stream()
                .map(deviceMapper::cpeDataFromUserExpAssoc)
                .sorted(Comparator.comparing(CpeData::getName))
                .filter(e -> e.getRssi() != null)
                .collect(Collectors.toList());
    }


    private List<AssociatedHosts.WifiLan> mapToWifiLanList(final Map<Instant, Integer> wifiMap, Session session) {
        return wifiMap.entrySet().stream()
                .map(e -> new AssociatedHosts.WifiLan(
                        e.getKey(),
                        DateTimeUtils.format(
                                DateTimeUtils.serverToClient(
                                        e.getKey().plus(3, ChronoUnit.HOURS),
                                        session.getClientType(), session.getZoneId()),
                                "Z",
                                "Default",
                                "Default"),
                        e.getValue()))
                .sorted(Comparator.comparing(AssociatedHosts.WifiLan::getCreatedIso))
                .collect(Collectors.toList());
    }

    private boolean isWifi(final NetworkMap host) {
        if (host.getInterfaceType() == null || host.getInterfaceType().isEmpty()) {
            if (host.getLayer1Interface() != null
                    && !host.getLayer1Interface().isEmpty()
                    && (host.getLayer1Interface().contains("Fi")
                    || host.getLayer1Interface().contains("WiFi")
                    || host.getLayer1Interface().contains("Wi-Fi"))) {
                return true;
            }
            return host.getLayer3Interface() != null
                    && !host.getLayer3Interface().isEmpty()
                    && (host.getLayer3Interface().contains("Fi")
                    || host.getLayer3Interface().contains("WiFi")
                    || host.getLayer3Interface().contains("Wi-Fi"));
        }
        return host.getInterfaceType().contains("Fi")
                || host.getInterfaceType().contains("WiFi")
                || host.getInterfaceType().contains("Wi-Fi")
                || host.getInterfaceType().startsWith("802.11");
    }

    public RssiHostsResponse getUserExperienceHostsRSSI(String token, LongIdRequest request) {
        final Session session = jwtService.getSession(token);

        final Optional<CpeEntity> cpe = cpeRepository.findById(request.getId());
        if (!cpe.isPresent()) {
            throw new FriendlyIllegalArgumentException(DEVICE_NOT_FOUND);
        }
        final String serial = cpe.get().getSerial();

        final Instant yesterday =
                DateTimeUtils.clientToServer(
                        Instant.now().minus(1, ChronoUnit.DAYS), session.getClientType(), session.getZoneId());

        final List<CpeData> cpeData = getCpeData(serial, yesterday);

        if (cpeData.isEmpty()) {
            return new RssiHostsResponse();
        }
        Map<String, Boolean> checkedMacs = new HashMap<>();

        final List<NetworkMap> hosts = getHosts(serial, yesterday);

        if (hosts.isEmpty()) {
            return new RssiHostsResponse();
        }

        Map<String, Integer> rssiValues = new HashMap<>();
        List<RssiHost> rssiHosts = new ArrayList<>();

        for (NetworkMap item : hosts) {
            if (item.getMac() == null || item.getMac().isEmpty()) {
                continue;
            }

            Boolean macOk;
            if (!checkedMacs.containsKey(item.getMac())) {
                Integer strength = getStrength(item.getMac(), cpeData);
                macOk = strength != 0;
                rssiValues.put(item.getHostname(), strength);
                checkedMacs.put(item.getMac(), macOk);
            } else {
                macOk = checkedMacs.get(item.getMac());
            }

            if (Boolean.FALSE.equals(macOk)) {
                continue;
            }

            rssiHosts.add(
                    new RssiHost(item.getHostname(), rssiValues.get(item.getHostname()),
                            item.getCreated(),  DateTimeUtils.format(
                            DateTimeUtils.serverToClient(
                                    item.getCreated().plus(3, ChronoUnit.HOURS),
                                    session.getClientType(), session.getZoneId()),
                            "Z",
                            "Default",
                            "Default")));
        }
        return new RssiHostsResponse(
                rssiHosts.stream()
                        .filter(h -> h.getValue() != 0)
                        .sorted(Comparator.comparing(RssiHost::getCreatedIso))
                        .collect(Collectors.toList()));
    }

    private Integer getStrength(String mac, List<CpeData> cpeData) {

        for (CpeData item : cpeData) {

            if(item.getRssi() == null && item.getSignal() == null
                    || !item.getMac().toLowerCase().equals(mac)) {
                continue;
            }

            int rssi;
            try {
                rssi = Integer.parseInt(item.getRssi());
            } catch (NumberFormatException e) {
                continue;
            }
            if (rssi > 0) {
                rssi = -rssi;
            }

            if (rssi > -9 || rssi < -100) {
                continue;
            }
            return rssi;
        }
        return 0;
    }

    public GetSpeedTestResponse getUserExperienceSpeedTest(String token, UserExperienceBody request) {
        Session session = jwtService.getSession(token);

        Optional<CpeEntity> cpe = cpeRepository.findById(request.getDeviceId());
        if (!cpe.isPresent()) {
            throw new FriendlyIllegalArgumentException(DEVICE_NOT_FOUND);
        }
        String serial = cpe.get().getSerial();

        Specification<DiagSpeedTestEntity> specification =
                qoeFrameService.getListFilersForSpeedTest(
                        Arrays.asList(
                                ConditionFilter.builder()
                                        .compare(ConditionType.Equal)
                                        .conditionString(String.valueOf(serial))
                                        .build(),
                                deviceMapper.userExperienceConditionToConditionType(request.getConditions())),
                        session.getClientType(),
                        session.getZoneId());
        try {
            List<DiagSpeedTestEntity> entities = diagSpeedRepository.findAll(specification);

            List<SpeedTest> uploadEntities =
                    entities.stream()
                            .filter(entity -> entity.getName().equals(UploadDiagnostic) && entity.getValue() != null)
                            .map((DiagSpeedTestEntity entity1)
                                    -> qoeFrameMapper.speedTestEntityToObject(entity1, session.getClientType(),
                                    session.getZoneId()))
                            .sorted(Comparator.comparing(SpeedTest::getCreatedIso))
                            .collect(Collectors.toList());

            List<SpeedTest> downloadEntities =
                    entities.stream()
                            .filter(entity -> entity.getName().equals(DownloadDiagnostic) && entity.getValue() != null)
                            .map((DiagSpeedTestEntity entity1)
                                    -> qoeFrameMapper.speedTestEntityToObject(entity1, session.getClientType(),
                                    session.getZoneId()))
                            .sorted(Comparator.comparing(SpeedTest::getCreatedIso))
                            .collect(Collectors.toList());

            return new GetSpeedTestResponse(uploadEntities, downloadEntities);
        } catch (CannotCreateTransactionException ex) {
            throw new FriendlyPermissionException(DATABASE_NOT_FOUND, "clickhouse");
        }
    }
}