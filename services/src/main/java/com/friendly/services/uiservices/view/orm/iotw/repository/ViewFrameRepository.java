package com.friendly.services.uiservices.view.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewFrameEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewFramePK;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link ViewFrameEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ViewFrameRepository extends BaseJpaRepository<ViewFrameEntity, ViewFramePK> {

    List<ViewFrameEntity> findAllByViewId(final Long viewId);

    void deleteAllByFrameId(Long frameId);

    List<ViewFrameEntity> findAllByViewIdOrderByIndex(final Long viewId);

    List<ViewFrameEntity> findAllByViewIdAndFrameIdNotIn(final Long viewId, final List<Long> frameIds);

    @Modifying
    int deleteAllByViewId(final Long viewId);

    @Modifying
    int deleteAllByViewIdAndFrameIdIn(final Long viewId, final List<Long> frameIds);

    @Modifying
    int deleteAllByFrameIdIn(final List<Long> frameIds);
}
