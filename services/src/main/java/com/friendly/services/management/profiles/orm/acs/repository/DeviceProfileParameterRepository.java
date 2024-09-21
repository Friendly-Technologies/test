package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceProfileParameterRepository extends BaseJpaRepository<DeviceProfileParameterEntity, Integer> {
    List<DeviceProfileParameterEntity> findAllByProfileId(Integer id);
}
