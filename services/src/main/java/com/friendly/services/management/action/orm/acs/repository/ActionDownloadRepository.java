package com.friendly.services.management.action.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.action.orm.acs.model.ActionDownloadEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionDownloadRepository extends BaseJpaRepository<ActionDownloadEntity, Integer> {
}
