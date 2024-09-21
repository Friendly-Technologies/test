package com.friendly.services.infrastructure.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.infrastructure.config.provider.ObjectMapperProvider;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class EntityDTOMapper {

    public static ObjectMapper mapper = ObjectMapperProvider.getObjectMapper();

    public static Map<Class<? extends Serializable>, Class<? extends Serializable>> entityDtoMap = new HashMap<>();

    public static <E, D extends Serializable> E dtoToEntity(D dto) {
        Optional<? extends Class<? extends Serializable>> eClassObj =
                entityDtoMap.entrySet()
                            .stream()
                            .filter(entry -> Objects.equals(entry.getValue(), dto.getClass()))
                            .map(Map.Entry::getKey)
                            .findFirst();

        if (eClassObj.isPresent()) {
            Class<? extends Serializable> entityClass = eClassObj.get();
            return mapper.convertValue(dto, (Class<E>) entityClass);
        }
        return null;
    }

    public static <E extends AbstractEntity, D extends Serializable> E dtoToEntity(D dto, Class<E> entityClass) {
        return mapper.convertValue(dto, entityClass);
    }

    public static <E, D extends Serializable> D entityToDto(E entity, Class<D> dtoClass) {
        if (entity == null) {
            return null;
        }

        if (dtoClass == null) {
            dtoClass = (Class<D>) entityDtoMap.get(entity.getClass());

            if (dtoClass == null) {
                return null;
            }
        }

        return mapper.convertValue(entity, dtoClass);
    }

    public static <E, D extends Serializable> D entityToDto(E entity) {
        return entityToDto(entity, null);
    }

    public static <E, D extends Serializable> List<D> entitiesToDtos(List<E> entities) {
        return entitiesToDtos(entities, null);
    }

    public static <E, D extends Serializable> List<D> entitiesToDtos(List<E> entities, Class<D> dtoClass) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>(0);
        }

        return entities.stream()
                       .map(entity -> (D) entityToDto(entity, dtoClass))
                       .collect(Collectors.toList());
    }

    public static <E, D extends Serializable> List<E> dtosToEntities(List<D> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>(0);
        }
        return dtos.stream()
                   .map(dto -> (E) dtoToEntity(dto))
                   .collect(Collectors.toList());
    }

    public static <E extends AbstractEntity,
            D extends Serializable> List<E> dtosToEntities(List<D> dtos, Class<E> entityClass) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>(0);
        }
        return dtos.stream()
                   .map(dto -> (E) dtoToEntity(dto, entityClass))
                   .collect(Collectors.toList());
    }
}
