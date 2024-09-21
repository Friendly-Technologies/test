package com.friendly.services.management.events.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.events.orm.acs.model.EventReceiverUrlEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventReceiverUrlRepository extends BaseJpaRepository<EventReceiverUrlEntity, Integer> {
    @Query("select f.id from EventReceiverUrlEntity f")
    List<Integer> getIds();
}
