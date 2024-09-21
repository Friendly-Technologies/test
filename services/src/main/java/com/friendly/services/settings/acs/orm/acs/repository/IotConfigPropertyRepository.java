package com.friendly.services.settings.acs.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.IotConfigPropertyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link IotConfigPropertyEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface IotConfigPropertyRepository extends BaseJpaRepository<IotConfigPropertyEntity, Long> {

    @Query("SELECT c FROM IotConfigPropertyEntity c WHERE c.groupId = :groupId " +
            "and (:domainId is null and (c.domainId is null or c.domainId = 0) or c.domainId = :domainId) " +
            "ORDER BY c.name ASC")
    List<IotConfigPropertyEntity> findAllByGroupIdAndDomainId(final Integer groupId, final Integer domainId);

    @Query("SELECT CASE WHEN count(p)>0 THEN true else false end FROM IotConfigPropertyEntity p " +
            "WHERE p.groupId = :id")
    boolean isExists(Integer id);
}
