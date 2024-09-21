package com.friendly.services.filemanagement.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.filemanagement.orm.acs.model.DeviceFileUploadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DeviceFileUploadEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceFileUploadRepository extends BaseJpaRepository<DeviceFileUploadEntity, Long> {

    Page<DeviceFileUploadEntity> findAllByDeviceId(final Long deviceId, final Pageable pageable);

    List<DeviceFileUploadEntity> findAllByDeviceId(final Long deviceId);

    Optional<DeviceFileUploadEntity> findByIdAndDeviceId(final Long id, final Long deviceId);

    @Transactional
    @Modifying
    int deleteByIdAndDeviceId(final Long id, final Long deviceId);

}
