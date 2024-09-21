package com.friendly.services.device.info.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AddMonitoringParamsBody;
import com.friendly.commons.models.device.MonitoringGraphBody;
import com.friendly.commons.models.device.StartStopMonitoringGraphBody;
import com.friendly.commons.models.device.monitoring.MonitoringDetail;
import com.friendly.commons.models.device.monitoring.MonitoringGraph;
import com.friendly.commons.models.device.monitoring.ParameterMonitoring;
import com.friendly.commons.models.device.response.MonitoringDetailsResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.customization.Customization;
import com.friendly.services.device.info.orm.iotw.model.DeviceMonitoringEntity;
import com.friendly.services.device.info.orm.iotw.model.MonitoringGraphEntity;
import com.friendly.services.device.info.orm.iotw.repository.DeviceMonitoringGraphRepository;
import com.friendly.services.device.info.orm.iotw.repository.DeviceMonitoringRepository;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import com.friendly.services.settings.sessions.SessionService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.MONITORING_NOT_FOUND;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceMonitoringService {

    @NonNull
    private final UserService userService;

    @NonNull
    private final SessionService sessionService;

    @NonNull
    private final ParameterService parameterService;

    private final ParameterNameService parameterNameService;

    @NonNull
    private final DeviceMonitoringRepository monitoringRepository;

    @NonNull
    private final DeviceMonitoringGraphRepository graphRepository;

    @NonNull
    private final JwtService jwtService;


    @Transactional
    public MonitoringDetailsResponse addDeviceMonitoring(final String token, final AddMonitoringParamsBody body) {
        final Session session = jwtService.getSession(token);
        final Long deviceId = body.getDeviceId();
        final List<Long> nameIds = body.getNameIds();

        monitoringRepository.saveAll(nameIds.stream()
                .filter(nameId -> !monitoringRepository.findByDeviceIdAndNameId(deviceId,
                                nameId)
                        .isPresent())
                .map(id -> DeviceMonitoringEntity.builder()
                        .sessionHash(session.getSessionHash())
                        .deviceId(deviceId)
                        .nameId(id)
                        .build())
                .collect(Collectors.toList()));

        return new MonitoringDetailsResponse(getMonitoringDetails(session.getClientType(), deviceId));
    }

    @Transactional
    public MonitoringDetailsResponse getDeviceMonitoring(final String token, final Long deviceId) {
        Session session = jwtService.getSession(token);

        return new MonitoringDetailsResponse(getMonitoringDetails(session.getClientType(), deviceId));
    }

    @Transactional
    public void deleteDeviceMonitoring(final String token, final AddMonitoringParamsBody body) {
        jwtService.getSession(token);
        final List<Long> nameIds = body.getNameIds();
        final  Long deviceId = body.getDeviceId();

        nameIds.forEach(nameId -> monitoringRepository.findByDeviceIdAndNameId(deviceId, nameId)
                .map(DeviceMonitoringEntity::getId)
                .ifPresent(graphRepository::deleteMonitoringGraph));
        monitoringRepository.deleteMonitoringParam(deviceId, nameIds);
    }

    public MonitoringGraph getMonitoringGraph(final String token, final MonitoringGraphBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final Long deviceId = body.getDeviceId();
        final Long nameId = body.getNameId();

        final Long monitoringId = monitoringRepository.findByDeviceIdAndNameId(deviceId, nameId)
                .map(DeviceMonitoringEntity::getId)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(
                        MONITORING_NOT_FOUND));
        final String fullName = parameterNameService.getNameById(nameId);
        final String type = parameterNameService.getTypeById(nameId);
        final List<ParameterMonitoring> params =
                graphRepository.findAllByMonitoringId(monitoringId)
                        .stream()
                        .map(p -> ParameterMonitoring.builder()
                                .value(p.getValue())
                                .timeIso(p.getTime())
                                .time(DateTimeUtils.format(p.getTime(),
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat()))
                                .build())
                        .collect(Collectors.toList());

        return MonitoringGraph.builder()
                .nameId(nameId)
                .fullName(fullName)
                .shortName(ParameterUtil.getShortName(fullName))
                .type(type)
                .parameters(params)
                .build();
    }

    @Transactional
    public void startStopMonitoringGraph(final String token, final StartStopMonitoringGraphBody body) {
        final Session session = jwtService.getSession(token);
        Long deviceId = body.getDeviceId();
        List<Long> nameIds = body.getNameIds();


        switch (body.getAction()) {
            case START:
                nameIds.forEach(nameId -> {
                    final Optional<DeviceMonitoringEntity> monitoringEntityOptional =
                            monitoringRepository.findByDeviceIdAndNameId(deviceId, nameId)
                                    .map(m -> monitoringRepository.saveAndFlush(
                                            m.toBuilder()
                                                    .active(true)
                                                    .build()));
                    final DeviceMonitoringEntity monitoringEntity =
                            monitoringEntityOptional.orElseGet(() -> monitoringRepository.saveAndFlush(
                                    DeviceMonitoringEntity.builder()
                                            .deviceId(deviceId)
                                            .nameId(nameId)
                                            .sessionHash(session.getSessionHash())
                                            .active(true)
                                            .build()));

                    graphRepository.saveAndFlush(MonitoringGraphEntity.builder()
                            .monitoringId(monitoringEntity.getId())
                            .value(parameterService.getParamValue(deviceId, nameId))
                            .time(Instant.now())
                            .build());
                });
                break;
            case STOP:
                nameIds.forEach(nameId -> monitoringRepository.findByDeviceIdAndNameId(deviceId, nameId)
                        .map(m -> monitoringRepository.saveAndFlush(
                                m.toBuilder()
                                        .active(false)
                                        .build())));
                break;
        }
    }

    private Set<MonitoringDetail> getMonitoringDetails(ClientType clientType, final Long deviceId) {
        final Map<Boolean, List<DeviceMonitoringEntity>> monitoringEntities =
                monitoringRepository.findAllByDeviceId(deviceId)
                        .stream()
                        .collect(Collectors.groupingBy(m -> Objects.nonNull(
                                sessionService.getSession(m.getSessionHash()))));

        final ArrayList<DeviceMonitoringEntity> defaultValue = new ArrayList<>();
        monitoringEntities.getOrDefault(Boolean.FALSE, defaultValue)
                .stream()
                .map(DeviceMonitoringEntity::getSessionHash)
                .forEach(this::deleteMonitoringBySessionHash);

        List<Long> defaultMonitorIds = Customization.getDefaultMonitoringListForClient(clientType);

        final LinkedHashSet<MonitoringDetail> monitoringDetails =
                monitoringEntities.getOrDefault(Boolean.TRUE, defaultValue)
                        .stream()
                        .filter(e -> Objects.nonNull(sessionService.getSession(e.getSessionHash())))
                        .map(e -> {
                            final String value = parameterService.getParamValue(deviceId, e.getNameId());
                            final String fullName = parameterNameService.getNameById(e.getNameId());
                            return MonitoringDetail.builder()
                                    .nameId(e.getNameId())
                                    .value(value)
                                    .isActive(e.isActive())
                                    .isDefault(defaultMonitorIds.contains(e.getNameId()))
                                    .fullName(fullName)
                                    .shortName(ParameterUtil.getShortName(fullName))
                                    .build();
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        monitoringDetails.addAll(
                parameterService.findNameIdValueByCpeIdAndNameIds(deviceId, defaultMonitorIds)
                        .stream()
                        .map(p -> MonitoringDetail.builder()
                                .nameId(p.getNameId())
                                .fullName(parameterNameService.getNameById(p.getNameId()))
                                .shortName(ParameterUtil.getShortName(parameterNameService.getNameById(p.getNameId())))
                                .isActive(false)
                                .isDefault(true)
                                .value(p.getValue())
                                .build())
                        .collect(Collectors.toList()));

        return monitoringDetails;
    }

    private void deleteMonitoringBySessionHash(final String sessionHash) {
        monitoringRepository.findAllBySessionHash(sessionHash)
                .stream()
                .map(DeviceMonitoringEntity::getId)
                .forEach(graphRepository::deleteMonitoringGraph);
        monitoringRepository.deleteBySessionHash(sessionHash);
    }
}
