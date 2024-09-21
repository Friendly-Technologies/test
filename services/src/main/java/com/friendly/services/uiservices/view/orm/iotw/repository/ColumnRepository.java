package com.friendly.services.uiservices.view.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewColumnPK;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link ColumnEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ColumnRepository extends BaseJpaRepository<ColumnEntity, ViewColumnPK> {

    List<ColumnEntity> findAllByViewId(final Long viewId);

    @Modifying
    void deleteAllByViewId(final Long viewId);
}
