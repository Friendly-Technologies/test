package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.RebootTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.RebootTaskActionResponse;
import com.ftacs.RebootTask;
import com.ftacs.UpdateTaskWS;

import java.util.ArrayList;
import java.util.List;

public class ActionRebootTaskConverter implements ActionConverter<RebootTaskAction, RebootTaskActionResponse> {
    @Override
    public UpdateTaskWS convertToEntity(RebootTaskAction request) {
        final RebootTask rebootTask = new RebootTask();
        rebootTask.setOrder(request.getOrder());
        return rebootTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.REBOOT_TASK);
        final List<ActionParameters> parameters = new ArrayList<>();
        parameters.add(ActionParameters.<RebootTaskActionResponse>builder()
                .name("Reboot")
                .details(null)
                .build());
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final RebootTask rebootTask = new RebootTask();
        rebootTask.setOrder(action.getPriority());
        updateTaskWSList.add(rebootTask);
        return updateTaskWSList;
    }
}
