package com.friendly.services.management.action.utils.converter;


import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.getparam.GetTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.GetTaskActionResponse;
import com.ftacs.GetEntryListWS;
import com.ftacs.GetEntryWS;
import com.ftacs.GetTask;
import com.ftacs.UpdateTaskWS;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ActionGetTaskConverter implements ActionConverter<GetTaskAction, GetTaskActionResponse> {
    private final ParameterNameService parameterNameService;

    @Override
    public GetTask convertToEntity(GetTaskAction request) {
        final GetTask getTask = new GetTask();
        final GetEntryListWS entryListWS = new GetEntryListWS();

        getTask.setOrder(request.getOrder());

        request.getParameters().forEach(e -> {
            GetEntryWS entry = new GetEntryWS();
            entry.setAttributes(e.isAttributes());
            entry.setNames(e.isNames());
            entry.setValue(e.getFullName());
            entry.setValues(e.isValues());
            entryListWS.getFullname().add(entry);
        });
        getTask.setParameters(entryListWS);
        return getTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.GET_TASK);
        final List<ActionParameters> parameters = new ArrayList<>();
        action.getActionGetParamList().forEach(e -> {
            String value = (Boolean.TRUE.equals(e.getValuesAttributes()) ? "values" : "") +
                    (Boolean.TRUE.equals(e.getNames()) ? " names" : "") +
                    (Boolean.TRUE.equals(e.getAttributes()) ? " attributes" : "");
            String parameterName = parameterNameService.getNameById(e.getNameId());
            ActionParameters actionParameter = ActionParameters.<GetTaskActionResponse>builder()
                    .name(parameterName)
                    .value(value)
                    .details(GetTaskActionResponse.builder()
                                    .names(e.getNames())
                                    .attributes(e.getAttributes())
                                    .values(e.getValuesAttributes())
                                    .fullName(parameterName)
                                    .build())
                    .build();
            parameters.add(actionParameter);
        });
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final GetTask getTask = new GetTask();
        final GetEntryListWS entryListWS = new GetEntryListWS();

        getTask.setOrder(action.getPriority());

        action.getActionGetParamList().forEach(e -> {
            GetEntryWS entry = new GetEntryWS();
            entry.setValue(parameterNameService.getNameById(e.getNameId()));
            entry.setAttributes(e.getAttributes());
            entry.setNames(e.getNames());
            entry.setValues(e.getValuesAttributes());
            entryListWS.getFullname().add(entry);
        });
        getTask.setParameters(entryListWS);
        updateTaskWSList.add(getTask);
        return updateTaskWSList;

    }
}
