package com.friendly.services.management.events.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.events.orm.acs.model.EventHardcodedUrlEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventHardcodedUrlRepository extends BaseJpaRepository<EventHardcodedUrlEntity, Integer> {
}
