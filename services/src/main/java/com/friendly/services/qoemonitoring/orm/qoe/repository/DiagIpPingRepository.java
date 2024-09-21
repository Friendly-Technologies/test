package com.friendly.services.qoemonitoring.orm.qoe.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagIpPingEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagIpPingEntityId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagIpPingRepository extends BaseJpaRepository<DiagIpPingEntity, DiagIpPingEntityId>,
        JpaSpecificationExecutor<DiagIpPingEntity> {
}
