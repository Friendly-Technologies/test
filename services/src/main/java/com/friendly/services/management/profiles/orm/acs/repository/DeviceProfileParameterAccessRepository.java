package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterAccessEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceProfileParameterAccessRepository extends BaseJpaRepository<DeviceProfileParameterAccessEntity, Integer> {
    List<DeviceProfileParameterAccessEntity> findAllByProfileId(Integer id);
}
