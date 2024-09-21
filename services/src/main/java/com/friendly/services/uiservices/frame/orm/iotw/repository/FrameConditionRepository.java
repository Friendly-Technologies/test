package com.friendly.services.uiservices.frame.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameConditionEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface FrameConditionRepository extends BaseJpaRepository<FrameConditionEntity, Long> {

    List<FrameConditionEntity> findAllByViewIdAndParentId(final Long viewId, final Long parentId);

    void deleteAllByViewId(Long viewId);
}
