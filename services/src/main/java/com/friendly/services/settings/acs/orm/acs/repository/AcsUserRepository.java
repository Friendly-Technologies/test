package com.friendly.services.settings.acs.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.AcsUserEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link AcsUserEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface AcsUserRepository extends BaseJpaRepository<AcsUserEntity, String>,
                                           JpaSpecificationExecutor<AcsUserEntity> {

}
