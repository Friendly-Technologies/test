package com.friendly.services.device.parameterstree.mapper;

import static com.friendly.services.device.parameterstree.utils.ParameterUtil.getShortName;

import com.friendly.commons.models.device.response.ParameterName;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.ParameterNameIdTypeProjection;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import org.springframework.stereotype.Component;

@Component
public class ParameterMapper {

    public ParameterName toParameterName(CpeParameterNameEntity entity) {
        return ParameterName.builder()
                .shortName(ParameterUtil.getShortName(entity.getName()))
                .type(ParameterUtil.getType(entity.getType()))
                .nameId(entity.getId())
                .build();
    }

    public ParameterName entityToParameterName(ParameterNameIdTypeProjection entity) {
        return ParameterName.builder()
                .nameId(entity.getId())
                .shortName(ParameterUtil.getShortName(entity.getName(), true))
                .type(entity.getType())
                .build();
    }
}