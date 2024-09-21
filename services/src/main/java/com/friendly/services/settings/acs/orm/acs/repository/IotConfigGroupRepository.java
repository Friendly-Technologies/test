package com.friendly.services.settings.acs.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.IotConfigGroupEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link IotConfigGroupEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface IotConfigGroupRepository extends BaseJpaRepository<IotConfigGroupEntity, Integer> {

    List<IotConfigGroupEntity> findAllByNameLike(final String name);
}
