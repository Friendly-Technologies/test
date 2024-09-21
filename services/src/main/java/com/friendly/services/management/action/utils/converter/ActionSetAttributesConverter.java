package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.setattrib.SetAttributesTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.SetAttributesTaskActionResponse;
import com.friendly.services.management.profiles.entity.deviceprofile.enums.DeviceProfileNotificationEnum;
import com.ftacs.AccessListWS;
import com.ftacs.CpeParamAttribListWS;
import com.ftacs.CpeParamAttribWS;
import com.ftacs.SetAttributesTask;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ActionSetAttributesConverter implements ActionConverter<SetAttributesTaskAction, SetAttributesTaskActionResponse> {

    private final ParameterNameService parameterNameService;

    @Override
    public UpdateTaskWS convertToEntity(SetAttributesTaskAction request) {
        final SetAttributesTask setAttributesTask = new SetAttributesTask();
        final CpeParamAttribListWS cpeParamAttribListWS = new CpeParamAttribListWS();
        setAttributesTask.setOrder(request.getOrder());
        request.getCpeParamAttributeList().forEach(e -> {
            CpeParamAttribWS cpeParamAttribWS = new CpeParamAttribWS();
            AccessListWS accessListWS = new AccessListWS();
            if(e.getAccessList().equals("Default")){
                cpeParamAttribWS.setAccessList(null);
            }else {
                accessListWS.getString().add(e.getAccessList().equals("All") ? "Subscriber" : "");
                cpeParamAttribWS.setAccessList(accessListWS);
            }
            cpeParamAttribWS.setNotification(DeviceProfileNotificationEnum.getValueByDescription(e.getNotification()));
            cpeParamAttribWS.setName(e.getFullName());
            cpeParamAttribWS.setReprovision(true);
            cpeParamAttribListWS.getCPEParamAttrib().add(cpeParamAttribWS);
        });
        setAttributesTask.setParameters(cpeParamAttribListWS);
        return setAttributesTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.SET_ATTRIBUTES_TASK);
        List<ActionParameters> parameters = new ArrayList<>();
        action.getActionSetAttributesList()
                .forEach(a -> {
                            String name = parameterNameService.getNameById(a.getNameId());
                            parameters.add(
                                    ActionParameters.<SetAttributesTaskActionResponse>builder()
                                            .name(name)
                                            .value(("Notification=" + DeviceProfileNotificationEnum.getDescriptionByValue(a.getNotification())) +
                                                    ("|".equals(a.getAccessList()) ? " Access=AcsOnly"
                                                            : ("|Subscriber".equals(a.getAccessList())  ? " Access=All" : " Access=Default")))
                                            .details(SetAttributesTaskActionResponse.builder()
                                                    .accessList("|".equals(a.getAccessList()) ? "AcsOnly" : ("|Subscriber".equals(a.getAccessList()) ? "All" : "Default"))
                                                    .notification(DeviceProfileNotificationEnum.getDescriptionByValue(a.getNotification()))
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
        final SetAttributesTask setAttributesTask = new SetAttributesTask();
        final CpeParamAttribListWS cpeParamAttribListWS = setAttributesTask.getParameters();
        setAttributesTask.setOrder(action.getPriority());
        action.getActionSetAttributesList().forEach(e -> {
            CpeParamAttribWS cpeParamAttribWS = new CpeParamAttribWS();
            AccessListWS accessListWS = new AccessListWS();
            if(e.getAccessList().equals("Default")){
                cpeParamAttribWS.setAccessList(null);
            }else {
                accessListWS.getString().add(e.getAccessList().equals("All") ? "Subscriber" : "");
                cpeParamAttribWS.setAccessList(accessListWS);
            }
            cpeParamAttribWS.setNotification(e.getNotification());
            cpeParamAttribWS.setReprovision(true);
            cpeParamAttribWS.setAccessList(accessListWS);
            cpeParamAttribWS.setName(parameterNameService.getNameById(e.getNameId()));
            cpeParamAttribListWS.getCPEParamAttrib().add(cpeParamAttribWS);
        });
        setAttributesTask.setParameters(cpeParamAttribListWS);
        updateTaskWSList.add(setAttributesTask);
        return updateTaskWSList;
    }
}
