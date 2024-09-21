package com.friendly.services.settings.alerts.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.alerts.orm.iotw.model.AlertsEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link AlertsEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface AlertsRepository extends BaseJpaRepository<AlertsEntity, ClientType> {
}
