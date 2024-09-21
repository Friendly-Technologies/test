package com.friendly.services.management.action.utils.converter;

import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.ftacs.UpdateTaskWS;

import java.util.List;


public interface ActionConverter<R, C> {
    UpdateTaskWS convertToEntity(R request);

    ActionListResponse convertToResponse(ActionEntity action);


    List<UpdateTaskWS> convertToRequest(ActionEntity action);
}
