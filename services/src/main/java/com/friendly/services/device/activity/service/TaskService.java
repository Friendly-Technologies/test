package com.friendly.services.device.activity.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.models.device.FTTaskTypesEnum;
import com.friendly.commons.models.device.TaskList;
import com.friendly.commons.models.device.TaskStateType;
import com.friendly.services.device.activity.orm.acs.model.AbstractCpeTaskEntity;
import com.friendly.services.device.activity.orm.acs.model.CpeCompletedTaskEntity;
import com.friendly.services.device.activity.orm.acs.model.CpeFailedTaskEntity;
import com.friendly.services.device.activity.orm.acs.model.CpePendingTaskEntity;
import com.friendly.services.device.activity.orm.acs.model.CpeRejectedTaskEntity;
import com.friendly.services.device.activity.orm.acs.repository.TaskRepository;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.utils.NumberConversionRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DENIED_DOMAIN;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    @NonNull
    private final TaskRepository taskRepository;
    private final JwtService jwtService;
    private final DeviceUtils deviceUtils;

    public Long getTaskKey(final Long transactionId) {
        return taskRepository.getPendingTasksByTransactionId(transactionId)
                .stream()
                .findFirst()
                .map(CpePendingTaskEntity::getTaskKey)
                .orElseGet(() -> taskRepository
                        .getRejectedTasksByTransactionId(transactionId)
                        .stream()
                        .findFirst()
                        .map(CpeRejectedTaskEntity::getTaskKey)
                        .orElseGet(() -> taskRepository
                                .getCompletedTasksByTransactionId(transactionId)
                                .stream()
                                .findFirst()
                                .map(CpeCompletedTaskEntity::getTaskKey)
                                .orElseGet(() -> taskRepository
                                        .getFailedTasksByTransactionId(transactionId)
                                        .stream()
                                        .findFirst()
                                        .map(CpeFailedTaskEntity::getTaskKey)
                                        .orElse(null))));
    }

    public List<Long> getTaskIds(final Long transactionId) {
        final List<Long> ids = new ArrayList<>();
        ids.addAll(taskRepository.getPendingTasksByTransactionId(transactionId)
                .stream()
                .map(AbstractCpeTaskEntity::getId)
                .collect(Collectors.toList()));
        ids.addAll(taskRepository.getRejectedTasksByTransactionId(transactionId)
                .stream()
                .map(AbstractCpeTaskEntity::getId)
                .collect(Collectors.toList()));
        ids.addAll(taskRepository.getCompletedTasksByTransactionId(transactionId)
                .stream()
                .map(AbstractCpeTaskEntity::getId)
                .collect(Collectors.toList()));
        ids.addAll(taskRepository.getFailedTasksByTransactionId(transactionId)
                .stream()
                .map(AbstractCpeTaskEntity::getId)
                .collect(Collectors.toList()));
        return ids;
    }

    public void deleteDiagnosticFromPendingTask(final Long deviceId, final Long diagnosticId) {
        taskRepository.deleteDiagnosticFromPendingTask(deviceId, diagnosticId);
    }


    public TaskStateType getTaskStateForParameter(Long deviceId, FTTaskTypesEnum taskType, Integer nameId) {
        List<Object[]> list = taskRepository.getDeviceActivityForType(deviceId, taskType.getCode());
        for (Object[] arr : list) {
            long taskKey = NumberConversionRegistry.convertToLong(arr[2]);
            if (nameId.equals((int) taskKey)) {
                return TaskStateType.fromValue((String) arr[0]);
            }
            String taskName = (String) arr[1];
            String[] numbers = StringUtils.substringAfter(taskName, "|")
                    .split("\\|");
            for (String num : numbers) {
                if (!num.isEmpty() && nameId.equals(Integer.valueOf(num))) {
                    return TaskStateType.fromValue((String) arr[0]);
                }
            }
        }
        return null;
    }

    public TaskList getTasks(final String token, final Long deviceId) {

        jwtService.getSession(token);
        if (deviceUtils.validateDeviceDomain(token, deviceId)) {
            return TaskList.builder()
                    .completedTasks(taskRepository.getCompletedTaskCount(deviceId))
                    .failedTasks(taskRepository.getFailedTaskCount(deviceId))
                    .pendingTasks(taskRepository.getPendingTaskCount(deviceId))
                    .rejectedTasks(taskRepository.getRejectedTaskCount(deviceId))
                    .sentTasks(taskRepository.getSentTaskCount(deviceId))
                    .build();
        } else {
            throw new FriendlyEntityNotFoundException(DENIED_DOMAIN);
        }
    }
}
