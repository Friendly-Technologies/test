package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupReactivate;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateGroupRactivateRepository extends BaseJpaRepository<UpdateGroupReactivate, Integer> {
}
