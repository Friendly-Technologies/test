package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileFilterEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceProfileFilterRepository extends BaseJpaRepository<DeviceProfileFilterEntity, Long> {
    DeviceProfileFilterEntity findByProfileId(Integer profileId);
}
