package com.friendly.services.device.provision.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DeviceProvisionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceProvisionRepository extends BaseJpaRepository<DeviceProvisionEntity, Long>,
                                                   PagingAndSortingRepository<DeviceProvisionEntity, Long> {

    boolean existsByIdAndCpeId(final Long id, final Long cpeId);

    Optional<DeviceProvisionEntity> findByIdAndCpeId(final Long id, final Long cpeId);

    Page<DeviceProvisionEntity> findAllByCpeId(final Long cpeId, final Pageable pageable);

    @Query("SELECT d FROM DeviceProvisionEntity d WHERE d.cpeId = :cpeId AND d.reprovision > 0 order by d.priority")
    Page<DeviceProvisionEntity> findAllProvisionsByCpeId(final Long cpeId, final Pageable pageable);
}
