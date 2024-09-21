package com.friendly.services.uiservices.frame.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link FrameEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface FrameRepository extends BaseJpaRepository<FrameEntity, Long> {

    List<FrameEntity> findAllByIdInAndIsDefault(final List<Long> ids, final Boolean isDefault);

    @Query("SELECT fv FROM FrameEntity fv WHERE fv.isDefault=true AND fv.name IN :names")
    List<FrameEntity> findAllByNameInAndIsDefaultTrue(final List<String> names);

    Boolean existsByName(final String name);

    Boolean existsByNameAndIdNot(final String name, final Long id);

    @Modifying
    int deleteAllByIdIn(final List<Long> ids);

}
