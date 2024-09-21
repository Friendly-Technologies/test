package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupDeviceEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.DeviceStateProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link UpdateGroupDeviceEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface UpdateGroupDeviceRepository extends BaseJpaRepository<UpdateGroupDeviceEntity, Integer> {

    @Query("SELECT ugd FROM UpdateGroupDeviceEntity ugd " +
            "WHERE ugd.groupUpdateChild.parent.id = :updateGroupId " +
            "AND (:groupId is null OR ugd.groupUpdateChild.productClass.id = :groupId)" +
            "AND (:serial is null OR ugd.device.serial = :serial)")
    Page<UpdateGroupDeviceEntity> findAllByUpdateGroupIdAndSerial(Integer updateGroupId, Long groupId,
                                                                  String serial, Pageable p);

    @Query("SELECT ugd FROM UpdateGroupDeviceEntity ugd " +
            "WHERE ugd.groupUpdateChild.parent.id = :updateGroupId " +
            "AND (:groupId is null OR ugd.groupUpdateChild.productClass.id = :groupId)" +
            "AND (:serial is null OR ugd.device.serial like :serial)")
    Page<UpdateGroupDeviceEntity> findAllByUpdateGroupIdAndSerialLike(Integer updateGroupId, Long groupId,
                                                                      String serial, Pageable p);

    @Query("SELECT case when count(ugd)> 0 then true else false end FROM UpdateGroupDeviceEntity ugd " +
            "WHERE ugd.groupUpdateChild.parent.id = :updateGroupId " +
            "AND (:groupId is null OR ugd.groupUpdateChild.productClass.id = :groupId)")
    boolean existsByUpdateGroupId(Integer updateGroupId, Long groupId);

    @Query("SELECT ugd.cpeId FROM UpdateGroupDeviceEntity ugd WHERE ugd.groupUpdateChildId = :updateGroupId")
    List<Long> findCpeIdsByUpdateGroupId(Integer updateGroupId);


    @Query("SELECT d.id, d.serial, ud.id as selected, udc.state  FROM DeviceEntity d " +
            "LEFT JOIN UpdateGroupDeviceEntity ud ON ud.cpeId = d.id " +
            "LEFT JOIN UpdateGroupCompletedEntity udc ON udc.deviceId = d.id " +
            "where d.productClass.groupId=:groupId")
    Page<DeviceStateProjection> findAllDevicesByGroupId(Long groupId, Pageable p);

    @Query("SELECT d.id, d.serial, ud.id as selected, udc.state  FROM DeviceEntity d " +
            "LEFT JOIN UpdateGroupDeviceEntity ud ON ud.cpeId = d.id " +
            "LEFT JOIN UpdateGroupCompletedEntity udc ON udc.deviceId = d.id " +
            "where d.productClass.groupId=:groupId AND lower(d.serial) LIKE lower(:searchParam)")
    Page<DeviceStateProjection> findAllDevicesByGroupIdAndSerial(Long groupId, String searchParam, Pageable p);

    @Query("SELECT COUNT(ugd) FROM UpdateGroupDeviceEntity ugd WHERE ugd.groupUpdateChild.parent.id IN :ids")
    long countByUpdateGroupIds(List<Integer> ids);

/*    @Query("SELECT count(ct.id) as completed, case when pt.repeats=0 then count(pt.id) else 0 end as pending, " +
            "case when pt.repeats>0 then count(pt.id) else 0 end as sent, count(rt.id) as rejected, count(ft.id) as failed " +
            "FROM UpdateGroupTransactionEntity t " +
            "left join CpeCompletedTaskEntity ct ON ct.transactionId = t.transactionId AND ct.cpeId = :deviceId " +
            "left join CpePendingTaskEntity pt ON pt.transactionId = t.transactionId AND pt.cpeId = :deviceId " +
            "left join CpeRejectedTaskEntity rt ON rt.transactionId = t.transactionId AND rt.cpeId = :deviceId " +
            "left join CpeFailedTaskEntity ft ON ft.transactionId = t.transactionId AND ft.cpeId = :deviceId " +
            "WHERE t.updateGroupId = :updateGroupId")
    Map<String, Object> getCountTasks(Integer updateGroupId, Long deviceId);*/
}
