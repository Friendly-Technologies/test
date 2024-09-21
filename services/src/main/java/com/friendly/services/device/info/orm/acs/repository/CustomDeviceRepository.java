package com.friendly.services.device.info.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseReadOnlyJpaRepository;
import com.friendly.services.device.info.orm.acs.model.CustomDeviceEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link CustomDeviceEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface CustomDeviceRepository extends BaseReadOnlyJpaRepository<CustomDeviceEntity, String> {

}
