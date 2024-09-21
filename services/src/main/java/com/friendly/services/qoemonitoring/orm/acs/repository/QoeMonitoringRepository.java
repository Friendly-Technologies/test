package com.friendly.services.qoemonitoring.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.acs.model.QoeMonitoringEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QoeMonitoringRepository extends BaseJpaRepository<QoeMonitoringEntity, Integer> {

    @Query("select distinct m.groupId from QoeMonitoringEntity m")
    List<Long> getUsedGroupId();

}
