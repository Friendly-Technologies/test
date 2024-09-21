package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceProfileRepository extends BaseJpaRepository<DeviceProfileEntity, Integer>, PagingAndSortingRepository<DeviceProfileEntity, Integer> {
    @Query("SELECT dp, pcg.model, pcg.manufacturerName " +
            "FROM DeviceProfileEntity dp " +
            "LEFT JOIN ProductClassGroupEntity pcg " +
            "ON dp.groupId = pcg.id " +
            "WHERE (:manufacturer IS NULL OR pcg.manufacturerName = :manufacturer) AND " +
            "(:modelName IS NULL OR pcg.model = :modelName) AND " +
            "(:status IS NULL OR :status = 2 OR :status = dp.status)")
    Page<Object[]> getAllByParams(Pageable p, String manufacturer, String modelName, Integer status);

    @Query("SELECT MAX(dp.id) " +
            "FROM DeviceProfileEntity dp " +
            "WHERE dp.groupId = :productId " +
            "AND dp.version = :version")
    Integer getMaxId(Integer productId, String version);

    @Query("SELECT dp " +
            "FROM DeviceProfileEntity dp " +
            "WHERE dp.groupId = :groupId " +
            "ORDER BY dp.id DESC")
    Page<DeviceProfileEntity> getLastProfileByGroupId(Long groupId, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT pu.send_backup_for_existing FROM profile_upload pu where pu.profile_id = :profileId")
    Boolean getProfileBackupInfo(Integer profileId);

    @Query("SELECT pcg.model, pcg.manufacturerName " +
            "FROM DeviceProfileEntity dp " +
            "LEFT JOIN ProductClassGroupEntity pcg " +
            "ON dp.groupId = pcg.id " +
            "WHERE dp.id = :id")
    List<Object[]> getConditions(Integer id);

    @Query(value = "SELECT p FROM DeviceProfileEntity p " +
            "LEFT JOIN DeviceProfileEventMonitorEntity em ON em.profileId = p.id " +
            "LEFT JOIN DeviceProfileParameterMonitorEntity pm ON pm.profileId = p.id " +
            "LEFT JOIN ActionEntity ug ON (ug.ugId = em.id OR ug.ugId = pm.id) " +
            "WHERE ug.ownerType = :ownerType AND ug.ugId = :automationId")
    DeviceProfileEntity getProfileByAutomationId(Integer automationId, Integer ownerType);

}
