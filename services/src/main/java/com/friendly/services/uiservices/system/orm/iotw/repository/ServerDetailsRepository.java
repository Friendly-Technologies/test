package com.friendly.services.uiservices.system.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.system.orm.iotw.model.ServerDetailsEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link ServerDetailsEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ServerDetailsRepository extends BaseJpaRepository<ServerDetailsEntity, Long> {

    void deleteAllByServerDetailsId(Long serverDetailsId);

}
