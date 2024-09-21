package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeCustomParameterEntity;
import com.friendly.services.management.profiles.orm.acs.model.GroupConditionFilterEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link CpeCustomParameterEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface GroupConditionFilterRepository extends BaseJpaRepository<GroupConditionFilterEntity, Long> {
    @Transactional
    @Modifying
    void deleteAllByGroupConditionId(Long groupConditionId);

    List<GroupConditionFilterEntity> findAllByGroupConditionId(Long groupConditionId);
}
