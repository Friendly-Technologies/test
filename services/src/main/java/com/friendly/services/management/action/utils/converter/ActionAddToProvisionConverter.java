package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.profiles.orm.acs.repository.DeviceProfileParameterNotificationRepository;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.AddToProvisionTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.AddToProvisionTaskActionResponse;
import com.ftacs.AddToProvisionTask;
import com.ftacs.UpdateTaskWS;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ActionAddToProvisionConverter implements ActionConverter<AddToProvisionTaskAction, AddToProvisionTaskActionResponse> {
    private final DeviceProfileParameterNotificationRepository profileParameterNotificationRepository;
    @Override
    public UpdateTaskWS convertToEntity(AddToProvisionTaskAction request) {
        final AddToProvisionTask addToProvisionTask = new AddToProvisionTask();
        addToProvisionTask.setOrder(request.getOrder());
        return addToProvisionTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.ADD_TO_PROVISION_TASK);
        final List<ActionParameters> parameters = new ArrayList<>();
        parameters.add(ActionParameters.
                <AddToProvisionTaskActionResponse>builder()
                .name(profileParameterNotificationRepository.findNotificationNameByMonitorId(action.getUgId()))
                .details(null)
                .build());
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateGroupTask = new ArrayList<>();
        final AddToProvisionTask addToProvisionTask = new AddToProvisionTask();
        addToProvisionTask.setOrder(action.getPriority());
        updateGroupTask.add(addToProvisionTask);
        return updateGroupTask;
    }

}
