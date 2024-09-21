package com.friendly.services.device.activity.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.AcsUserEntity;
import com.friendly.services.device.activity.orm.acs.model.DeviceActivityDetailsEntity;
import com.friendly.services.device.activity.orm.acs.model.DeviceActivitySetAttribDetailsEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link AcsUserEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceActivityDetailsRepository extends BaseJpaRepository<DeviceActivityDetailsEntity, Serializable>,
                                           JpaSpecificationExecutor<DeviceActivityDetailsEntity> {
    @Query(nativeQuery = true)
    List<DeviceActivityDetailsEntity> getCreatorFromFileHistory(Long id);

    @Query(nativeQuery = true)
    List<DeviceActivityDetailsEntity> getTaskParamFromProvisionHistory(Long id);

    @Query(nativeQuery = true)
    List<DeviceActivityDetailsEntity> getTaskParamFromObjectHistory(Long id);

    @Query(nativeQuery = true)
    List<DeviceActivityDetailsEntity> getTaskParamFromObjectParamHistory(Long id);

    List<DeviceActivityDetailsEntity> getTaskParamFromProvAttrHis(Long id);

    @Query(nativeQuery = true)
    List<DeviceActivityDetailsEntity> getTaskParamFromCustomRpcHistory(Long id);

    @Query(nativeQuery = true)
    List<DeviceActivityDetailsEntity> getTaskParamFromOp(Long id);

    @Query(nativeQuery = true)
    List<DeviceActivityDetailsEntity> getTaskParamFromDeleteObject(Long id);

    List<DeviceActivitySetAttribDetailsEntity> getTaskParamForSetAttribute(Long id);
}
