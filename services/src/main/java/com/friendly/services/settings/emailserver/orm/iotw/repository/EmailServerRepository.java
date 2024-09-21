package com.friendly.services.settings.emailserver.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.emailserver.orm.iotw.model.EmailServerEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link EmailServerEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface EmailServerRepository extends BaseJpaRepository<EmailServerEntity, ClientType> {
}
