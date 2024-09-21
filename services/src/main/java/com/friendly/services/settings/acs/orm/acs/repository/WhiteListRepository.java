package com.friendly.services.settings.acs.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.WhiteListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link WhiteListEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface WhiteListRepository extends BaseJpaRepository<WhiteListEntity, Integer>,
                                             PagingAndSortingRepository<WhiteListEntity, Integer> {

    Page<WhiteListEntity> findAllByIpRangeIsNotNull(final Pageable pageable);

    Page<WhiteListEntity> findAllByIpRangeIsNull(final Pageable pageable);
}
