package com.friendly.services.settings.acs.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.CpeSerialEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link CpeSerialEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface CpeSerialRepository extends BaseJpaRepository<CpeSerialEntity, Long> {

    long countByProtocolId(final Integer protocolId);
}
