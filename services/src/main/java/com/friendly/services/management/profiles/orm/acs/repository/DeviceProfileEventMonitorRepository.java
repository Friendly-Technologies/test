package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileEventMonitorEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceProfileEventMonitorRepository extends BaseJpaRepository<DeviceProfileEventMonitorEntity, Integer> {
    List<DeviceProfileEventMonitorEntity> findAllByProfileId(Integer id);
}
