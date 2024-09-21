package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterMonitorEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceProfileParameterMonitorRepository extends BaseJpaRepository<DeviceProfileParameterMonitorEntity, Integer> {
    List<DeviceProfileParameterMonitorEntity> findAllByProfileId(Integer id);
}
