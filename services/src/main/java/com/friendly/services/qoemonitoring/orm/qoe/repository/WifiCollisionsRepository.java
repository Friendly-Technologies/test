package com.friendly.services.qoemonitoring.orm.qoe.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.WifiCollisionEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.WifiCollisionEntityId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WifiCollisionsRepository extends BaseJpaRepository<WifiCollisionEntity, WifiCollisionEntityId>,
        JpaSpecificationExecutor<WifiCollisionEntity> {}
