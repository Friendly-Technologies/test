package com.friendly.services.settings.snmpserver;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.settings.SnmpServer;
import com.friendly.commons.models.settings.request.SnmpServerRequest;
import com.friendly.commons.models.settings.response.SnmpServerResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.snmpserver.orm.iotw.model.SnmpServerEntity;
import com.friendly.services.settings.snmpserver.orm.iotw.repository.SnmpServerRepository;
import com.friendly.services.settings.snmpserver.mapper.SnmpServerMapper;
import com.friendly.services.settings.snmpserver.sender.SnmpSenderFactory;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.UserActivityType.CONFIGURING_SNMP;
import static com.friendly.commons.models.websocket.ActionType.CREATE;
import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.commons.models.websocket.SettingType.SNMP_SERVER;

/**
 * Service that exposes the base functionality for interacting with {@link SnmpServer} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SnmpServerService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final SnmpServerRepository snmpRepository;

    @NonNull
    private final SnmpServerMapper snmpMapper;

    @NonNull
    private final WsSender wsSender;

    @NonNull
    private final StatisticService statisticService;

    /**
     * Get SnmpServer Setting
     *
     * @return {@link SnmpServer} setting
     */
    public SnmpServerResponse getSnmpServer(final String token) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        return getSnmpServer(clientType);
    }

    public List<SnmpServer> getAllSnmpServers(final ClientType clientType) {
        return snmpRepository.findAll()
                .stream()
                .map(snmpMapper::entityToSnmpServer)
                .collect(Collectors.toList());
    }

    public SnmpServerResponse getSnmpServer(final ClientType clientType) {
        return snmpMapper.entityToSnmpServerResponse(getSnmpServerEntity(clientType));
    }

    private SnmpServerEntity getSnmpServerEntity(final ClientType clientType) {
        return snmpRepository.findById(clientType)
                .orElseGet(() -> getSnmpServerEntity(clientType));
    }

    /**
     * Update EmailServer Setting
     *
     * @return {@link SnmpServer} setting
     */
    @Transactional
    public SnmpServerResponse updateSnmpServer(final String token, final SnmpServerRequest snmpServer) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        final SnmpServerEntity oldSnmpServerEntity =
                snmpRepository.findById(clientType)
                        .orElse(null);
        final SnmpServer result;
        if (oldSnmpServerEntity == null) {
            result = snmpMapper.entityToSnmpServer(snmpRepository.saveAndFlush(snmpMapper.snmpServerToEntity(
                            clientType,
                            snmpServer)));
            wsSender.sendSettingEvent(clientType, CREATE, SNMP_SERVER, result);
            String note = "Set Host=" + result.getHost() + "; Port=" + result.getPort() +
                    "; Community=" + result.getCommunity() + "; Version=" + result.getVersion() + ";";
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(clientType)
                    .activityType(CONFIGURING_SNMP)
                    .note(note)
                    .build());
        } else {
            final SnmpServer oldSnmpServer = snmpMapper.entityToSnmpServer(oldSnmpServerEntity);
            if (!snmpServer.equals(oldSnmpServer)) {
                result = snmpMapper.entityToSnmpServer(
                        snmpRepository.saveAndFlush(snmpMapper.snmpServerToEntity(
                                clientType,
                                snmpServer)));
                wsSender.sendSettingEvent(clientType, UPDATE, SNMP_SERVER, result);
                StringBuilder note = new StringBuilder().append("Set");
                if (ObjectUtils.notEqual(result.getHost(), snmpServer.getHost())) {
                    note.append(" Host=").append(result.getHost()).append(";");
                }
                if (ObjectUtils.notEqual(result.getPort(), snmpServer.getPort())) {
                    note.append(" Port=").append(result.getPort()).append(";");
                }
                if (ObjectUtils.notEqual(result.getCommunity(), snmpServer.getCommunity())) {
                    note.append(" Community=").append(result.getCommunity()).append(";");
                }
                if (ObjectUtils.notEqual(result.getVersion(), snmpServer.getVersion())) {
                    note.append(" Version=").append(result.getVersion()).append(";");
                }
                statisticService.addUserLogAct(UserActivityLog.builder()
                        .userId(session.getUserId())
                        .clientType(clientType)
                        .activityType(CONFIGURING_SNMP)
                        .note(note.toString())
                        .build());
            } else {
                return snmpMapper.requestToSnmpServerResponse(snmpServer);
            }
        }
        SnmpSenderFactory.putSnmpSender(result);
        return snmpMapper.modelToSnmpServerResponse(result);
    }

}
