package com.friendly.services.qoemonitoring.orm.qoe.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagSpeedTestEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.DiagSpeedTestEntityId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DiagSpeedRepository extends BaseJpaRepository<DiagSpeedTestEntity, DiagSpeedTestEntityId>,
        JpaSpecificationExecutor<DiagSpeedTestEntity> {}
