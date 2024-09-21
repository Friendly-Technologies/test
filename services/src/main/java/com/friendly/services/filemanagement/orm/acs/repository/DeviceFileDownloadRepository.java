package com.friendly.services.filemanagement.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.filemanagement.orm.acs.model.DeviceFileDownloadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DeviceFileDownloadEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceFileDownloadRepository extends BaseJpaRepository<DeviceFileDownloadEntity, Long> {

    Page<DeviceFileDownloadEntity> findAllByDeviceId(final Long deviceId, final Pageable pageable);

    Optional<DeviceFileDownloadEntity> findByIdAndDeviceId(final Long id, final Long deviceId);

    @Transactional
    @Modifying
    int deleteByIdAndDeviceId(final Long id, final Long deviceId);

}
