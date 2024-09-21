package com.friendly.services.settings.userinterface.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceDescriptionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link InterfaceDescriptionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface InterfaceDescriptionRepository extends BaseJpaRepository<InterfaceDescriptionEntity, Long> {
    Optional<InterfaceDescriptionEntity> findFirstByInterfaceDescriptionIdAndLocaleId(String interfaceDescriptionId, String localeId);
}
