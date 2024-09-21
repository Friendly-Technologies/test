package com.friendly.services.settings.alerts.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.alerts.orm.acs.model.AcsMonitoringDataEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AcsMonitoringDataRepository extends BaseJpaRepository<AcsMonitoringDataEntity, String>,
        JpaSpecificationExecutor<AcsMonitoringDataEntity> {
}
