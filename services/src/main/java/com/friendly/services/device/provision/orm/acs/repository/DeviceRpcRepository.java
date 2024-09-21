package com.friendly.services.device.provision.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.provision.orm.acs.model.DeviceRpcEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DeviceRpcEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceRpcRepository extends BaseJpaRepository<DeviceRpcEntity, Long>,
                                             PagingAndSortingRepository<DeviceRpcEntity, Long> {

    Optional<DeviceRpcEntity> findByIdAndCpeIdAndReprovision(final Long id, final Long cpeId,
                                                             final Integer reprovision);

    boolean existsByCpeIdAndReprovision(final Long cpeId, final Integer reprovision);

    Optional<DeviceRpcEntity> findByIdAndCpeId(final Long id, final Long cpeId);

    Page<DeviceRpcEntity> findAllByCpeIdAndReprovision(final Long cpeId, final Integer reprovision,
                                                       final Pageable pageable);
}
