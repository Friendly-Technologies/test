package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.UpdateGroupChildProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link UpdateGroupEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface UpdateGroupChildRepository extends BaseJpaRepository<UpdateGroupEntity, Integer> {

    @Query("SELECT d FROM DeviceEntity d " +
            "LEFT JOIN UpdateGroupChildEntity ugc ON ugc.productClass.id = d.productClass.productGroup.id " +
            "WHERE ugc.parent.id = :updateGroupId " +
            "AND (:groupId is null OR ugc.productClass.id = :groupId)" +
            "AND (:serial is null OR d.serial = :serial)")
    Page<DeviceEntity> findAllByUpdateGroupIdAndSerial(Integer updateGroupId, Long groupId,
                                                       String serial, Pageable p);

    @Query("SELECT d FROM DeviceEntity d " +
            "LEFT JOIN UpdateGroupChildEntity ugc ON ugc.productClass.id = d.productClass.productGroup.id " +
            "WHERE ugc.parent.id = :updateGroupId " +
            "AND (:groupId is null OR ugc.productClass.id = :groupId)" +
            "AND (:serial is null OR d.serial like :serial)")
    Page<DeviceEntity> findAllByUpdateGroupIdAndSerialLike(Integer updateGroupId, Long groupId,
                                                           String serial, Pageable p);



    @Query("SELECT ugc.id, ugc.groupId, ugc.parent.state as state FROM UpdateGroupChildEntity ugc " +
            "WHERE ugc.productClass.manufacturerName = :manufacturer AND ugc.productClass.model = :model")
    Optional<UpdateGroupChildProjection> findByManufacturerAndModel(String manufacturer, String model);

}
