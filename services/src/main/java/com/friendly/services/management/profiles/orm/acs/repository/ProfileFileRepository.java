package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.ProfileFileEntity;
import com.friendly.services.management.profiles.orm.acs.model.ProfileDownloadReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link ProfileFileEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ProfileFileRepository extends BaseJpaRepository<ProfileFileEntity, Long> {

    @Query("SELECT pf.id as profileId, p.name as profileName, pcg.manufacturerName as manufacturerName, pcg.model as model, " +
            "t.name as fileType, pf.created as created, pf.creator as creator, pf.url as url, p.version as profileVersion, " +
            "case when d.name is null then 'Super domain' else d.name end as domainName, " +
            "count(pt.id) as pending, count(rt.id) as rejected, count(ct.id) as completed, count(ft.id) as failed " +
            "FROM ProfileEntity p " +
            "INNER JOIN ProfileFileEntity pf ON pf.profileId = p.id " +
            "INNER JOIN ProductClassGroupEntity pcg ON p.groupId = pcg.id " +
            "INNER JOIN FileTypeEntity t ON pf.fileTypeId = t.id " +
            "LEFT JOIN DomainEntity d ON p.domainId = d.id " +
            "LEFT JOIN CpePendingTaskEntity pt ON pf.id = pt.taskKey and pt.typeId = 1 " +
            "LEFT JOIN CpeRejectedTaskEntity rt ON pf.id = rt.taskKey and rt.typeId = 1 " +
            "LEFT JOIN CpeCompletedTaskEntity ct ON pf.id = ct.taskKey and ct.typeId = 1 " +
            "LEFT JOIN CpeFailedTaskEntity ft ON pf.id = ft.taskKey and ft.typeId = 1 " +
            "WHERE (:id is null or pf.id = :id)" +
            "AND (:manufacturer is null or pcg.manufacturerName = :manufacturer) " +
            "AND (:model is null or pcg.model = :model) " +
            "AND (:domainIds is null or p.domainId IN :domainIds) " +
            "group by pf.id, p.name, pcg.manufacturerName, pcg.model, p.version, " +
            "case when d.name is null then 'Super domain' else d.name end")
    Page<ProfileDownloadReportProjection> findAll(Long id, List<Integer> domainIds, String manufacturer, String model,
                                                  Pageable pageable);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end as domain, pf.id as id, p.name as name, " +
            "pcg.manufacturerName as manufacturer, pcg.model as model, t.name as file_type, pf.created as created, " +
            "pf.creator as creator, p.version as version, count(ct.id) as completed, count(pt.id) as pending," +
            "count(rt.id) as rejected, count(ft.id) as failed " +
            "FROM ProfileEntity p " +
            "INNER JOIN ProfileFileEntity pf ON pf.profileId = p.id " +
            "INNER JOIN ProductClassGroupEntity pcg ON p.groupId = pcg.id " +
            "INNER JOIN FileTypeEntity t ON pf.fileTypeId = t.id " +
            "LEFT JOIN DomainEntity d ON p.domainId = d.id " +
            "LEFT JOIN CpePendingTaskEntity pt ON pf.id = pt.taskKey and pt.typeId = 1 " +
            "LEFT JOIN CpeRejectedTaskEntity rt ON pf.id = rt.taskKey and rt.typeId = 1 " +
            "LEFT JOIN CpeCompletedTaskEntity ct ON pf.id = ct.taskKey and ct.typeId = 1 " +
            "LEFT JOIN CpeFailedTaskEntity ft ON pf.id = ft.taskKey and ft.typeId = 1 " +
            "WHERE (:id is null or pf.id = :id)" +
            "AND (:manufacturer is null or pcg.manufacturerName = :manufacturer) " +
            "AND (:model is null or pcg.model = :model) " +
            "AND (:isIdsNull is true or p.domainId IN :domainIds) " +
            "group by pf.id, p.name, pcg.manufacturerName, pcg.model, p.version, " +
            "case when d.name is null then 'Super domain' else d.name end")
    List<Object[]> findAllForExcel(Long id, List<Integer> domainIds, boolean isIdsNull, String manufacturer, String model);

    @Query("SELECT DISTINCT p.groupId FROM ProfileEntity p")
    List<Long> getUsedGroupId();

}
