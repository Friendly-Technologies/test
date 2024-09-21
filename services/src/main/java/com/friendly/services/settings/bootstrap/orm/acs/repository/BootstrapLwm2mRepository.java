package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLwm2mEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository to interact with persistence layer to store {@link BootstrapLwm2mEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface BootstrapLwm2mRepository extends BaseJpaRepository<BootstrapLwm2mEntity, Integer>, JpaSpecificationExecutor<BootstrapLwm2mEntity> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE FROM lwm2m_bs_object_resources WHERE bs_id = ?1")
    void deleteAllResourcesByBsId(Integer id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE FROM lwm2m_bs_server WHERE bs_id = ?1")
    void deleteAllServersById(Integer id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE FROM lwm2m_bs_security WHERE bs_id = ?1")
    void deleteAllSecuritiesById(Integer id);
}
