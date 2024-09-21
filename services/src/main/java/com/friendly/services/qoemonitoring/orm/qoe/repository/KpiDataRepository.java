package com.friendly.services.qoemonitoring.orm.qoe.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.KpiDataEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.KpiDataEntityId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiDataRepository extends BaseJpaRepository<KpiDataEntity, KpiDataEntityId>,
        JpaSpecificationExecutor<KpiDataEntity> {
}
