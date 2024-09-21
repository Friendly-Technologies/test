package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupCompletedHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateGroupCompletedHistoryRepository extends BaseJpaRepository<UpdateGroupCompletedHistory, Integer> {

}
