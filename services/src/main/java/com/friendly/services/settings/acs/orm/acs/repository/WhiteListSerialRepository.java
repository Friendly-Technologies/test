package com.friendly.services.settings.acs.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.WhiteListSerialEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link WhiteListSerialEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface WhiteListSerialRepository extends BaseJpaRepository<WhiteListSerialEntity, Integer>,
                                                   PagingAndSortingRepository<WhiteListSerialEntity, Integer> {

    long countByWhiteListId(final Integer whiteListId);
}
