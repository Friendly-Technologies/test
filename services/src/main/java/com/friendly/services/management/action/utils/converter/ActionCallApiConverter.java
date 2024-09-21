package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.CallApiTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.CallApiTaskActionResponse;
import com.ftacs.CallApiTask;
import com.ftacs.UpdateTaskWS;

import java.util.ArrayList;
import java.util.List;


public class ActionCallApiConverter implements ActionConverter<CallApiTaskAction, CallApiTaskActionResponse> {


    @Override
    public UpdateTaskWS convertToEntity(CallApiTaskAction request) {
        final CallApiTask callApiTask = new CallApiTask();
        callApiTask.setApiMethodName(request.getApiMethodName());
        callApiTask.setApiRequest(request.getApiRequest());
        callApiTask.setApiUrl(request.getApiUrl());
        callApiTask.setOrder(request.getOrder());
        return callApiTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.CALL_API_TASK);
        List<ActionParameters> parameters = new ArrayList<>();
        action.getActionCallApiList()
                .forEach(e -> parameters.add(
                        ActionParameters.<CallApiTaskActionResponse>builder()
                                .name(e.getApiUrl())
                                .value(e.getApiMethodName())
                                .details(CallApiTaskActionResponse.builder()
                                        .apiMethodName(e.getApiMethodName())
                                        .apiUrl(e.getApiUrl())
                                        .apiRequest(e.getApiRequest())
                                        .build())
                                        .build()
                        )
                );
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final CallApiTask callApiTask = new CallApiTask();
        action.getActionCallApiList().forEach(e -> {
            callApiTask.setApiMethodName(e.getApiMethodName());
            callApiTask.setApiMethodName(e.getApiMethodName());
            callApiTask.setApiRequest(e.getApiRequest());
            callApiTask.setApiUrl(e.getApiUrl());
            callApiTask.setOrder(action.getPriority());
            updateTaskWSList.add(callApiTask);
        });
        return updateTaskWSList;
    }
}
