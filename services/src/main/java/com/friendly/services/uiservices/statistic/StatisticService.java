package com.friendly.services.uiservices.statistic;

import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.services.uiservices.statistic.orm.iotw.model.DeviceLogEntity;
import com.friendly.services.uiservices.statistic.orm.iotw.model.UserLogEntity;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.DeviceLogRepository;
import com.friendly.services.uiservices.statistic.orm.iotw.repository.UserLogRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service that exposes the base functionality for Activity Logs
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {

    @NonNull
    private final UserLogRepository userLogRepository;

    @NonNull
    private final DeviceLogRepository deviceLogRepository;

    @Async
    @Transactional
    public void addUserLogAct(final UserActivityLog userLog) {
        userLogRepository.save(UserLogEntity.builder()
                                            .clientType(userLog.getClientType())
                                            .userId(userLog.getUserId())
                                            .activityType(userLog.getActivityType())
                                            .note(userLog.getNote())
                                            .date(Instant.now())
                                            .build());
    }

    @Async
    public void addDeviceLogAct(final DeviceActivityLog deviceLog) {
        deviceLogRepository.save(DeviceLogEntity.builder()
                                                .clientType(deviceLog.getClientType())
                                                .userId(deviceLog.getUserId())
                                                .activityType(deviceLog.getActivityType())
                                                .deviceId(deviceLog.getDeviceId())
                                                .serial(deviceLog.getSerial())
                                                .groupId(deviceLog.getGroupId())
                                                .note(deviceLog.getNote())
                                                .date(Instant.now())
                                                .build());
    }
}
