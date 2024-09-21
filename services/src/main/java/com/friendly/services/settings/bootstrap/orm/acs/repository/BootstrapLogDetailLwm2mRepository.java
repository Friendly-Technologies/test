package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLogDetailLwm2mEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link BootstrapLogDetailLwm2mEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface BootstrapLogDetailLwm2mRepository extends BaseJpaRepository<BootstrapLogDetailLwm2mEntity, Integer> {

    List<BootstrapLogDetailLwm2mEntity> findAllByLogId(Integer logId);

    @Transactional
    @Modifying
    void deleteAllByLogIdIn(List<Integer> logId);

}
