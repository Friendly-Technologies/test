package com.friendly.services.management.events.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.events.orm.acs.model.EventMonitoringEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMonitoringRepository extends BaseJpaRepository<EventMonitoringEntity, Integer> {

    @Query("select distinct m.groupId from EventMonitoringEntity m")
    List<Long> getUsedGroupId();
}
