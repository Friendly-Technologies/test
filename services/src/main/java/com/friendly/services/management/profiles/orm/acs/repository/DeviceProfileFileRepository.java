package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileFileEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceProfileFileRepository extends BaseJpaRepository<DeviceProfileFileEntity, Integer> {
}
