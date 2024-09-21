package com.friendly.services.management.action.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.action.orm.acs.model.ActionCallApiEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionCallApiRepository extends BaseJpaRepository<ActionCallApiEntity, Integer> {
}
