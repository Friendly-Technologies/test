package com.friendly.services.device.provision.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionObjectEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.CpeParameterNameValueProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DeviceProvisionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceProvisionObjectRepository extends BaseJpaRepository<DeviceProvisionObjectEntity, Long>,
                                                         PagingAndSortingRepository<DeviceProvisionObjectEntity, Long> {


    boolean existsByIdAndCpeId(final Long id, final Long cpeId);

    Optional<DeviceProvisionObjectEntity> findByIdAndCpeId(final Long id, final Long cpeId);

    @Query("SELECT d FROM DeviceProvisionObjectEntity d WHERE d.cpeId = :cpeId AND d.reprovision > 0 order by d.priority")
    Page<DeviceProvisionObjectEntity> findAllByCpeId(final Long cpeId, final Pageable pageable);

    @Query("SELECT cp.name as name, cp.value as value FROM DeviceProvisionObjectParameterEntity cp WHERE cp.cpeProvisionObjectId = :id")
    List<CpeParameterNameValueProjection> findParamsByProvisionId(Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM cpe_provision_object_parameter cp " +
                    "WHERE cp.cpe_provision_object_id = ?1")
    void deleteParamsForProvision(Long provisionId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "INSERT INTO cpe_provision_object_parameter " +
                    "(cpe_provision_object_id, created, creator, name, value) " +
                    "VALUES(?1, ?3, ?2, ?4, ?5)")
    void saveProvisionParam(Long provisionId, String updater, Instant updated, String name, String value);
}
