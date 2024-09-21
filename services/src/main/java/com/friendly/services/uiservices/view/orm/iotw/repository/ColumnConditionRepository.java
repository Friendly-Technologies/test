package com.friendly.services.uiservices.view.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnConditionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link ColumnConditionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ColumnConditionRepository extends BaseJpaRepository<ColumnConditionEntity, Long> {

    List<ColumnConditionEntity> findAllByViewIdAndParentId(final Long viewId, final Long parentId);

    @Modifying
    int deleteAllByViewIdAndIdNotIn(final Long viewId, final List<Long> ids);

    @Modifying
    int deleteAllByViewId(final Long viewId);
}
