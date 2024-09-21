package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.RpcMethodTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.RpcMethodResponse;
import com.ftacs.RpcMethodTask;
import com.ftacs.RpcMethodWS;
import com.ftacs.UpdateTaskWS;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ActionRpcMethodConverter implements ActionConverter<RpcMethodTaskAction, RpcMethodResponse> {
    @Override
    public UpdateTaskWS convertToEntity(RpcMethodTaskAction request) {
        final RpcMethodTask rpcMethodTask = new RpcMethodTask();
        final RpcMethodWS rpcMethodWS = new RpcMethodWS();
        rpcMethodTask.setOrder(request.getOrder());
        rpcMethodWS.setMethodName(request.getMethod());
        rpcMethodWS.setReprovision(request.isReprovision());
        rpcMethodWS.setRequestMessage(request.getRequest());
        rpcMethodTask.setRpcMethod(rpcMethodWS);
        return rpcMethodTask;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        final ActionListResponse response = new ActionListResponse();
        final List<ActionParameters> parameters = new ArrayList<>();
        response.setTaskType(ActionTypeEnum.RPC_METHOD_TASK);
        action.getActionCustomRpcList().forEach(e ->
            parameters.add(ActionParameters.<RpcMethodResponse>builder()
                    .name("Custom RPC")
                    .value(e.getMethodName())
                    .details(RpcMethodResponse.builder()
                            .method(e.getMethodName())
                            .request(e.getMessage())
                            .build())
                    .build())
        );
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();
        final RpcMethodTask rpcMethodTask = new RpcMethodTask();
        action.getActionCustomRpcList().forEach(e -> {
            RpcMethodWS rpcMethodWS = new RpcMethodWS();
            rpcMethodTask.setOrder(action.getPriority());
            rpcMethodWS.setMethodName(e.getMethodName());
            rpcMethodWS.setRequestMessage(e.getMessage());
            rpcMethodTask.setRpcMethod(rpcMethodWS);
        });
        updateTaskWSList.add(rpcMethodTask);
        return updateTaskWSList;

    }
}
