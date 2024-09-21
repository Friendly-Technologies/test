package com.friendly.services.device.mapper;

import com.friendly.commons.models.device.UnusedModelsResponse;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {
    public UnusedModelsResponse productClassGroupToUnusedModelsResponse(ProductClassGroupEntity entity){
        return UnusedModelsResponse.builder()
                .id(entity.getId())
                .manufacturer(entity.getManufacturerName())
                .model(entity.getModel())
                .build();
    }
}
