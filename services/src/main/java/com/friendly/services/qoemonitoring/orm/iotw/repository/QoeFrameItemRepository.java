package com.friendly.services.qoemonitoring.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.iotw.model.QoeFrameItemEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QoeFrameItemRepository extends BaseJpaRepository<QoeFrameItemEntity, Long> {
    Optional<QoeFrameItemEntity> findByName(String name);
}
