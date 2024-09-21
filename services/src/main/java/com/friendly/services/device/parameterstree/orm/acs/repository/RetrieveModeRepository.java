package com.friendly.services.device.parameterstree.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.RetrieveModeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link RetrieveModeEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface RetrieveModeRepository extends BaseJpaRepository<RetrieveModeEntity, Integer>,
        PagingAndSortingRepository<RetrieveModeEntity, Integer> {

    @Query("SELECT m.productGroup.id from RetrieveModeEntity m")
    List<Integer> getIds();

}
