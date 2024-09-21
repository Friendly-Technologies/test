package com.friendly.services.device.info.utils;

import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.info.orm.iotw.model.MonitoringGraphEntity;
import com.friendly.services.device.info.orm.iotw.repository.DeviceMonitoringGraphRepository;
import com.friendly.services.device.info.orm.iotw.repository.DeviceMonitoringRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceMonitoringScheduler {

    @Value("${device.monitoring.period.sec}")
    private Long period;

    @NonNull
    private final TaskScheduler scheduler;

    @NonNull
    private final ParameterService parameterService;

    @NonNull
    private final DeviceMonitoringRepository monitoringRepository;

    @NonNull
    private final DeviceMonitoringGraphRepository graphRepository;

    @PostConstruct
    public void monitoring() {
        scheduler.scheduleAtFixedRate(this::monitoringTask, period * 1000);
    }

    @Transactional
    public void monitoringTask() {
        log.trace("run monitoring task");
        monitoringRepository.findAllByActiveIsTrue()
                            .forEach(monitoring -> {
                                final Long id = monitoring.getId();
                                final String lastValue =
                                        graphRepository.findFirstByMonitoringIdOrderByIdDesc(id)
                                                       .map(MonitoringGraphEntity::getValue)
                                                       .orElse(null);
                                final String currentValue =
                                        parameterService.getParamValue(monitoring.getDeviceId(),
                                                                                 monitoring.getNameId());

                                if (ObjectUtils.notEqual(currentValue, lastValue)) {
                                    graphRepository.saveAndFlush(MonitoringGraphEntity.builder()
                                                                                      .monitoringId(id)
                                                                                      .value(currentValue)
                                                                                      .time(Instant.now())
                                                                                      .build());
                                }
                            });
    }

}
