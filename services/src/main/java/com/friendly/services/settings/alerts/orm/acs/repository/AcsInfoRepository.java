package com.friendly.services.settings.alerts.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.alerts.orm.acs.model.AcsInfoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface AcsInfoRepository extends BaseJpaRepository<AcsInfoEntity, String>,
        JpaSpecificationExecutor<AcsInfoEntity> {

}
