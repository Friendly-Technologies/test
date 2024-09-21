package com.friendly.services.management.events.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.events.orm.acs.model.EventHardcodedEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventHardcodedRepository extends BaseJpaRepository<EventHardcodedEntity, Integer> {
}
