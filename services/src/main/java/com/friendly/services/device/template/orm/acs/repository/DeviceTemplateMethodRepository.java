package com.friendly.services.device.template.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.template.orm.acs.model.DeviceTemplateMethodEntity;
import com.friendly.services.device.template.orm.acs.model.DeviceTemplateMethodPK;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTemplateMethodRepository extends BaseJpaRepository<DeviceTemplateMethodEntity, DeviceTemplateMethodPK> {
}

