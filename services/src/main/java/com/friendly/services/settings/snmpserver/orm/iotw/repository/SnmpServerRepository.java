package com.friendly.services.settings.snmpserver.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.snmpserver.orm.iotw.model.SnmpServerEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link SnmpServerEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface SnmpServerRepository extends BaseJpaRepository<SnmpServerEntity, ClientType> {
}
