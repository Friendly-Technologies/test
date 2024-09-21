package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLogLwm2mEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link BootstrapLogLwm2mEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface BootstrapLogLwm2mRepository extends BaseJpaRepository<BootstrapLogLwm2mEntity, Integer>,
        JpaSpecificationExecutor<BootstrapLogLwm2mEntity> {

}
