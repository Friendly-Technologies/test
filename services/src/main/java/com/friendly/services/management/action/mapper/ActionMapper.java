package com.friendly.services.management.action.mapper;

import com.friendly.services.management.action.dto.response.MonitorActionResponse;
import com.friendly.commons.models.device.FTTaskTypesEnum;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.utils.converter.ActionConverter;
import com.friendly.services.management.action.utils.converter.ActionDiagnosticConverter;
import com.friendly.services.management.action.utils.converter.ActionUninstallOpConverter;
import com.friendly.services.management.action.utils.converter.ActionUpdateOpConverter;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.utils.ActionUtils;
import com.ftacs.UpdateGroupTaskWSList;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ActionMapper {
    public UpdateGroupTaskWSList convertToUpdateGroupTaskWSList(final List<AbstractActionRequest> requestList, final List<ActionEntity> actionEntities,
                                                                final Long groupId) {
        UpdateGroupTaskWSList updateGroupTaskWSList = new UpdateGroupTaskWSList();
        if(actionEntities != null && !actionEntities.isEmpty()) {
            actionEntities.forEach(a ->
                    updateGroupTaskWSList.getGetTaskOrSetValueTaskOrSetAttributeTask().addAll(convertExistingTaskToWS(a))
            );
        }
        updateGroupTaskWSList.getGetTaskOrSetValueTaskOrSetAttributeTask().addAll(convertToWSList(requestList, groupId));

        return updateGroupTaskWSList;
    }
    public UpdateGroupTaskWSList convertToUpdateGroupTaskWSList(List<AbstractActionRequest> requestList, Long groupId) {
        UpdateGroupTaskWSList updateGroupTaskWSList = new UpdateGroupTaskWSList();
        updateGroupTaskWSList.getGetTaskOrSetValueTaskOrSetAttributeTask().addAll(convertToWSList(requestList, groupId));
        return updateGroupTaskWSList;
    }
    public List<UpdateTaskWS> convertToWSList(final List<AbstractActionRequest> requestList,
                                                             final Long groupId) {
        if (requestList != null && !requestList.isEmpty()) {
            return requestList.stream()
                    .map(r -> convertTaskToWS(r.getTaskType(), r, groupId))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public <R, C> UpdateTaskWS convertTaskToWS(ActionTypeEnum taskType, R request, Long groupId) {
        ActionConverter<R, C> converter = (ActionConverter<R, C>) ActionUtils.CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.get(taskType);
        if (converter != null) {
            if (converter instanceof ActionDiagnosticConverter) {
                ((ActionDiagnosticConverter) converter).setGroupId(groupId);
            }
            if (converter instanceof ActionUpdateOpConverter) {
                ((ActionUpdateOpConverter) converter).setGroupId(groupId);
            }
            if (converter instanceof ActionUninstallOpConverter) {
                ((ActionUninstallOpConverter) converter).setGroupId(groupId);
            }
            return converter.convertToEntity(request);
        }
        throw new IllegalArgumentException("No converter found for action type: " + taskType);
    }

    public MonitorActionResponse convertToActionResponse(List<ActionEntity> actions, Long groupId) {
        return new MonitorActionResponse(convertToActionListResponse(actions, groupId));
    }

    public List<ActionListResponse> convertToActionListResponse(List<ActionEntity> actions, Long groupId) {
        return actions.stream()
                .map(a -> checkIsCustomRpc(a, groupId))
                .collect(Collectors.toList());
    }

    public ActionListResponse convertTaskToResponse(FTTaskTypesEnum taskType, ActionEntity action, Long groupId) {
        ActionConverter<?, ?> converter;
        if (Objects.requireNonNull(taskType) == FTTaskTypesEnum.ChangeDUStateUpdateGroup) {
            converter = getChangeDUStateConverter(action);
        } else {
            converter = ActionUtils.CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.get(taskType);
        }
        if (converter != null) {
            if (converter instanceof ActionUninstallOpConverter) {
                ((ActionUninstallOpConverter) converter).setGroupId(groupId);
            }
            return converter.convertToResponse(action);
        }
        throw new IllegalArgumentException("No converter found for action type: " + taskType);
    }

    private static ActionConverter<?, ?> getChangeDUStateConverter(ActionEntity action) {
        if (!action.getActionOpUninstallList().isEmpty()) {
            return ActionUtils.CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.get(ActionTypeEnum.UNINSTALL_TASK);
        }
        if (!action.getActionOpInstallList().isEmpty()) {
            return ActionUtils.CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.get(ActionTypeEnum.INSTALL_TASK);
        }
        if (!action.getActionOpUpdateList().isEmpty()) {
            return ActionUtils.CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.get(ActionTypeEnum.UPDATE_SOFTWARE_TASK);
        }
        throw new IllegalArgumentException("No converter found");
    }

    private <C> ActionListResponse checkIsCustomRpc(ActionEntity a, Long groupId) {
        if (!a.getActionCustomRpcList().isEmpty()) {
            return convertTaskToResponse(FTTaskTypesEnum.CustomRPCUpdateGroup, a, groupId);
        } else {
            return convertTaskToResponse(FTTaskTypesEnum.getByCode(a.getTaskType()), a, groupId);
        }
    }

    public List<UpdateTaskWS> convertExistingTaskToWS(ActionEntity action) {
        if (!action.getActionCustomRpcList().isEmpty()) {
            return convertTaskToRequest(FTTaskTypesEnum.CustomRPCUpdateGroup, action);
        } else {
            return convertTaskToRequest(FTTaskTypesEnum.getByCode(action.getTaskType()), action);
        }
    }

    private <C> List<UpdateTaskWS> convertTaskToRequest(FTTaskTypesEnum taskType, ActionEntity action) {
        ActionConverter<ActionListResponse, C> converter = (ActionConverter<ActionListResponse, C>) ActionUtils.CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.get(taskType);
        if (converter != null) {
            return converter.convertToRequest(action);
        }
        throw new IllegalArgumentException("No converter found for action type: " + taskType);
    }
}