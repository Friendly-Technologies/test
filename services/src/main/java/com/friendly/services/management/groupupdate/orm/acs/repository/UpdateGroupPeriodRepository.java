package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupPeriod;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateGroupPeriodRepository extends BaseJpaRepository<UpdateGroupPeriod, Integer> {
}
