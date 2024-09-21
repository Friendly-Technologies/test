package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.cpemethod.CpeMethodTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.CpeMethodResponse;
import com.ftacs.CpeMethodTask;
import com.ftacs.ParameterListWS;
import com.ftacs.ParameterWS;
import com.ftacs.UpdateTaskWS;

import java.util.ArrayList;
import java.util.List;

public class ActionCpeMethodConverter implements ActionConverter<CpeMethodTaskAction, CpeMethodResponse> {
    @Override
    public UpdateTaskWS convertToEntity(CpeMethodTaskAction request) {
        final CpeMethodTask cpeMethodTask = new CpeMethodTask();
        final ParameterListWS parameterListWS = new ParameterListWS();
        cpeMethodTask.setOrder(request.getOrder());
        cpeMethodTask.setCpeMethod(request.getCpeMethod().getMethod());
            ParameterWS parameterWS = new ParameterWS();
            parameterWS.setName(request.getCpeMethod().getMethod());
            parameterWS.setValue(request.getCpeMethod().getValue());
            parameterListWS.getParameter().add(parameterWS);
        cpeMethodTask.setParameters(parameterListWS);
        return cpeMethodTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse actionListResponse = new ActionListResponse();
        actionListResponse.setTaskType(ActionTypeEnum.CPE_METHOD_TASK);
        final List<ActionParameters> parameters = new ArrayList<>();
        if (!action.getActionSetValueList().isEmpty()) {
            String fullName = action.getMethodNameEntity().getName();
            action.getActionSetValueList().forEach(e -> {
                Integer instance = 0;
                for (char c : fullName.toCharArray()) {
                    if(Character.isDigit(c)){
                        instance = Integer.parseInt(c+"");
                    }
                }
                parameters.add(ActionParameters.<CpeMethodResponse>builder()
                        .name(fullName)
                        .value(e.getValue())
                        .details(CpeMethodResponse.builder()
                                .method(fullName)
                                .value(e.getValue())
                                .instance(instance)
                                .build())
                        .build());
                    });
        }
        actionListResponse.setOrder(action.getPriority());
        actionListResponse.setParameters(parameters);
        return actionListResponse;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final CpeMethodTask cpeMethodTask = new CpeMethodTask();
        final ParameterListWS parameterListWS = new ParameterListWS();
        cpeMethodTask.setOrder(action.getPriority());
        cpeMethodTask.setCpeMethod(action.getMethodNameEntity().getName());
        action.getActionSetValueList().forEach(e -> {
            ParameterWS parameterWS = new ParameterWS();
            parameterWS.setName(action.getMethodNameEntity().getName());
            parameterWS.setValue(e.getValue());
            parameterListWS.getParameter().add(parameterWS);
        });
        cpeMethodTask.setParameters(parameterListWS);
        updateTaskWSList.add(cpeMethodTask);
        return updateTaskWSList;
    }
}
