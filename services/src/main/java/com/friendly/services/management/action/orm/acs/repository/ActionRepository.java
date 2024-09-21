package com.friendly.services.management.action.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends BaseJpaRepository<ActionEntity, Integer> {

    List<ActionEntity> getAllByUgIdAndOwnerType(Integer ugId, Integer ownerType);

    Boolean existsActionEntityByUgIdAndOwnerType(Integer ugId, Integer ownerType);
}
