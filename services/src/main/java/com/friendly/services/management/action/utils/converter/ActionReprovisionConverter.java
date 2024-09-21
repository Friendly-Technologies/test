package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.ReprovisionTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ReprovisionTaskActionResponse;
import com.ftacs.ReprovisionTask;
import com.ftacs.UpdateTaskWS;

import java.util.ArrayList;
import java.util.List;


public class ActionReprovisionConverter implements ActionConverter<ReprovisionTaskAction, ReprovisionTaskActionResponse> {


    @Override
    public UpdateTaskWS convertToEntity(ReprovisionTaskAction request) {
        final ReprovisionTask reprovisionTask = new ReprovisionTask();
        reprovisionTask.setOrder(request.getOrder());
        reprovisionTask.setCpeFile(true);
        reprovisionTask.setCustomRPC(true);
        reprovisionTask.setSendProfile(true);
        reprovisionTask.setCpeProvisionObject(true);
        reprovisionTask.setSendCPEProvisionAttribute(true);
        reprovisionTask.setSendCPEProvision(true);
        return reprovisionTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.RE_PROVISION_TASK);
        response.setOrder(action.getPriority());
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();

        action.getActionReprovisionList().forEach(e -> {
            ReprovisionTask reprovisionTask = new ReprovisionTask();
            reprovisionTask.setOrder(action.getPriority());
            reprovisionTask.setCpeFile(e.getFile());
            reprovisionTask.setCustomRPC(e.getCustomRpc());
            reprovisionTask.setSendProfile(e.getProfile());
            reprovisionTask.setCpeProvisionObject(e.getProvObject());
            reprovisionTask.setSendCPEProvisionAttribute(e.getProvision());
            reprovisionTask.setSendCPEProvision(e.getProvision());
            updateTaskWSList.add(reprovisionTask);
        });

        return updateTaskWSList;
    }
}
