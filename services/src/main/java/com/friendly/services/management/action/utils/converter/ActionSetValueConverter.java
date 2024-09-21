package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.setvalue.SetValueTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.SetValueTaskActionResponse;
import com.ftacs.CpeParamListWS;
import com.ftacs.CpeParamWS;
import com.ftacs.SetValueTask;
import com.ftacs.UpdateTaskWS;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ActionSetValueConverter implements ActionConverter<SetValueTaskAction, SetValueTaskActionResponse> {
    private final ParameterNameService parameterNameService;


    @Override
    public UpdateTaskWS convertToEntity(SetValueTaskAction request) {
        final SetValueTask setValueTask = new SetValueTask();
        final CpeParamListWS cpeParamListWS = new CpeParamListWS();

        setValueTask.setOrder(request.getOrder());

        request.getCpeParamList().forEach(e -> {
            CpeParamWS cpeParamWS = new CpeParamWS();
            cpeParamWS.setReprovision(true);
            cpeParamWS.setValue(e.getValue());
            cpeParamWS.setName(e.getFullName());
            cpeParamListWS.getCPEParam().add(cpeParamWS);
        });
        setValueTask.setParameters(cpeParamListWS);
        return setValueTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.SET_VALUE_TASK);
        List<ActionParameters> parameters = new ArrayList<>();
        action.getActionSetValueList().forEach(e -> {
                    String name = parameterNameService.getNameById(e.getNameId());
                    parameters.add(ActionParameters.<SetValueTaskActionResponse>builder()
                            .name(name)
                            .value(e.getValue())
                                    .details(SetValueTaskActionResponse.builder()
                                            .value(e.getValue())
                                            .fullName(name)
                                            .build())
                            .build());
                }
        );
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final SetValueTask setValueTask = new SetValueTask();
        final CpeParamListWS cpeParamListWS = new CpeParamListWS();

        setValueTask.setOrder(action.getPriority());

        action.getActionSetValueList().forEach(e -> {
            CpeParamWS cpeParamWS = new CpeParamWS();
            cpeParamWS.setValue(e.getValue());
            cpeParamWS.setReprovision(true);
            cpeParamWS.setName(parameterNameService.getNameById(e.getNameId()));
            cpeParamListWS.getCPEParam().add(cpeParamWS);
        });
        setValueTask.setParameters(cpeParamListWS);
        updateTaskWSList.add(setValueTask);
        return updateTaskWSList;
    }
}
