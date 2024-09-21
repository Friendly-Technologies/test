package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.FactoryResetTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.FactoryResetTaskActionResponse;
import com.ftacs.FactoryResetTask;
import com.ftacs.UpdateTaskWS;

import java.util.ArrayList;
import java.util.List;

public class ActionFactoryResetConverter implements ActionConverter<FactoryResetTaskAction, FactoryResetTaskActionResponse> {

    @Override
    public UpdateTaskWS convertToEntity(FactoryResetTaskAction request) {
        final FactoryResetTask factoryResetTask = new FactoryResetTask();
        factoryResetTask.setOrder(request.getOrder());
        return factoryResetTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.FACTORY_RESET_TASK);
        final List<ActionParameters> parameters = new ArrayList<>();
        parameters.add(ActionParameters.<FactoryResetTaskActionResponse>builder()
                .name("Factory reset")
                .details(null)
                .build());
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final FactoryResetTask factoryResetTask = new FactoryResetTask();
        factoryResetTask.setOrder(action.getPriority());
        updateTaskWSList.add(factoryResetTask);
        return updateTaskWSList;
    }
}
