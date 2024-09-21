package com.friendly.services.uiservices.view.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.view.orm.iotw.model.ConditionItemEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionItemRepository extends BaseJpaRepository<ConditionItemEntity, Long> {

    Optional<ConditionItemEntity> findByViewId(Long viewId);

    @Query("SELECT c FROM ConditionItemEntity c " +
            "JOIN c.view v " +
            "WHERE v.domainId = ?1")
    List<ConditionItemEntity> findAllByDomainId(Integer domainId);

}
