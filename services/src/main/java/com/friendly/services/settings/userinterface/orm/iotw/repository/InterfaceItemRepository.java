package com.friendly.services.settings.userinterface.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceItemEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link InterfaceItemEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface InterfaceItemRepository extends BaseJpaRepository<InterfaceItemEntity, String> {
}
