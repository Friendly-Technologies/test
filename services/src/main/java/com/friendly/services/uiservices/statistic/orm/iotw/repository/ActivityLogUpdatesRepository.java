package com.friendly.services.uiservices.statistic.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.dto.enums.ActivationType;
import com.friendly.services.uiservices.statistic.orm.iotw.model.ActivityLogUpdates;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogUpdatesRepository extends BaseJpaRepository<ActivityLogUpdates, Integer> {
    @Query(nativeQuery = true,
            value = "select distinct act_type " +
            "from iotw.activity_log_updates " +
            "where update_id=?1 and act_type in (?2, ?3)")
    ActivationType getActivationMethod(Integer updateId, String actType1, String actType2);
}

