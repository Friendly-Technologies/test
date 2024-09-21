package com.friendly.services.device.info.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.info.orm.acs.model.CpeEntity;
import com.friendly.services.device.info.orm.acs.model.projections.CpeRegReportProjection;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.productclass.orm.acs.model.projections.ProductGroupEventReportProjection;
import com.friendly.services.productclass.orm.acs.model.projections.ProductGroupFirmwareReportProjection;
import com.friendly.services.productclass.orm.acs.model.projections.ProductGroupOnlineReportProjection;
import com.friendly.services.productclass.orm.acs.model.projections.ProductGroupWithCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link CpeEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface CpeRepository extends BaseJpaRepository<CpeEntity, Long>,
        PagingAndSortingRepository<CpeEntity, Long> {

    long countByProtocolId(final Integer protocolId);

    @Query("SELECT c.serial AS serial, c.protocolId AS protocolId, pc.groupId AS groupId " +
            "FROM CpeEntity c LEFT JOIN ProductClassEntity pc on pc.id = c.productClassId " +
            "WHERE c.id = :deviceId")
    CpeSerialProtocolIdGroupIdProjection findCpeSerialProtocolIdGroupIdProjectionByDeviceId(final Long deviceId);

    @Query("SELECT (case when d.name is null then 'Super domain' else d.name end) as domainName, " +
            "c.serial as serial, pc.manufacturerName as manufacturerName, pc.model as model, c.created as created, " +
            "c.updated as updated, min(cd.phone) as phone " +
            "FROM CpeEntity c INNER JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "INNER JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "INNER JOIN ProductClassGroupEntity pc ON p.groupId = pc.id " +
            "LEFT JOIN CustomDeviceEntity cd ON c.serial = cd.serial " +
            "WHERE (:manufacturer is null or pc.manufacturerName = :manufacturer) " +
            "AND (:model is null or pc.model = :model) " +
            "AND (:from is null or c.created >= :from) AND (:to is null or c.created <= :to) " +
            "AND (:isIdsNull is true or c.domainId IN :domainIds) " +
            "group by case when d.name is null then 'Super domain' else d.name end, c.serial, pc.manufacturerName, " +
            "pc.model, c.created, c.updated")
    Page<CpeRegReportProjection> getDeviceRegistrationReport(List<Integer> domainIds, boolean isIdsNull, String manufacturer, String model,
                                                             Instant from, Instant to, Pageable pageable);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end, " +
            "c.serial, pc.manufacturerName, pc.model, c.created, c.updated, " +
            "case when min(cd.phone) is null then '' else min(cd.phone) end " +
            "FROM CpeEntity c INNER JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "INNER JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "INNER JOIN ProductClassGroupEntity pc ON p.groupId = pc.id " +
            "LEFT JOIN CustomDeviceEntity cd ON c.serial = cd.serial " +
            "WHERE (:manufacturer is null or pc.manufacturerName = :manufacturer) " +
            "AND (:model is null or pc.model = :model) " +
            "AND (:from is null or c.created >= :from) AND (:to is null or c.created <= :to) " +
            "AND (:isIdsNull is true or c.domainId IN :domainIds) " +
            "group by case when d.name is null then 'Super domain' else d.name end, c.serial, pc.manufacturerName, " +
            "pc.model, c.created, c.updated")
    List<Object[]> getFullDeviceRegistrationReport(List<Integer> domainIds, boolean isIdsNull, String manufacturer, String model,
                                                   Instant from, Instant to);
    @Query("SELECT c.serial FROM CpeEntity c WHERE c.id = :deviceId")
    String getSerial(final Long deviceId);

    @Query("SELECT p.groupId " +
            "FROM CpeEntity c INNER JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "WHERE c.id = :deviceId")
    Integer getProductClassGroupId(final Long deviceId);


    @Query("SELECT c.protocolId FROM CpeEntity c WHERE c.id = :deviceId")
    Optional<Integer> getProtocolTypeByDevice(final Long deviceId);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end as domainName, " +
            "m.name as manufacturerName, p.model as model, count(c.id) as count " +
            "FROM CpeEntity c INNER JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "INNER JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:from is null or c.created >= :from) AND (:to is null or c.created <= :to) " +
            "AND c.domainId IN :domainIds " +
            "group by case when d.name is null then 'Super domain' else d.name end, m.name, p.model")
    List<ProductGroupWithCountProjection> getDeviceDistributionReport(List<Integer> domainIds, String manufacturer,
                                                                      Instant from, Instant to);

    @Query(nativeQuery = true, value = "SELECT case when d.name is null then 'Super domain' else d.name end as domainName, " +
            "m.name as manufacturerName, p.model as model, date(l.created) as date, count(distinct l.cpe_id) as count " +
            "FROM cpe c " +
            "INNER JOIN cpe_log l ON l.cpe_id = c.id " +
            "LEFT JOIN product_class p ON c.product_class_id = p.id " +
            "LEFT JOIN manufacturer m ON p.manuf_id = m.id " +
            "LEFT JOIN isp d ON c.location_id = d.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:from is null or l.created >= :from) AND (:to is null or l.created <= :to) " +
            "AND (:domainIds is null or c.location_id IN (:domainIds)) " +
            "group by case when d.name is null then 'Super domain' else d.name end, m.name, p.model, date(l.created)")
    List<ProductGroupOnlineReportProjection> getDeviceOnlineReportMySql(List<Integer> domainIds, String manufacturer, String model,
                                                                        @Temporal Date from, @Temporal Date to);

    @Query(nativeQuery = true, value = "SELECT case when d.name is null then 'Super domain' else d.name end as domainName, " +
            "m.name as manufacturerName, p.model as model, trunc(l.created) as date, " +
            "count(distinct l.cpe_id) as count " +
            "FROM cpe c " +
            "INNER JOIN cpe_log l ON l.cpe_id = c.id " +
            "LEFT JOIN product_class p ON c.product_class_id = p.id " +
            "LEFT JOIN manufacturer m ON p.manuf_id = m.id " +
            "LEFT JOIN isp d ON c.location_id = d.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:from is null or l.created >= :from) AND (:to is null or l.created <= :to) " +
            "AND (:domainIds is null or c.location_id IN (:domainIds)) " +
            "group by case when d.name is null then 'Super domain' else d.name end, " +
            "m.name, p.model, to_char(l.created, 'DD/MM/YYYY')")
    List<ProductGroupOnlineReportProjection> getDeviceOnlineReportOracle(List<Integer> domainIds, String manufacturer, String model,
                                                                         @Temporal Date from, @Temporal Date to);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end as domain, c.id as deviceId, " +
            "c.created as created, c.serial as serial, m.name as manufacturer, p.model as model, " +
            "l.created as last_session " +
            "FROM CpeEntity c " +
            "INNER JOIN DeviceHistoryEntity l ON l.deviceId = c.id " +
            "LEFT JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:from is null or l.created >= :from) AND (:to is null or l.created <= :to) " +
            "AND (:isIdsNull is true or c.domainId IN (:domainIds))")
    List<Object[]> getDeviceOnlineReport(List<Integer> domainIds, boolean isIdsNull, String manufacturer, String model,
                                                    Instant from, Instant to);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end as domainName, " +
            "m.name as manufacturerName, p.model as model, count(c.id) as count " +
            "FROM CpeEntity c " +
            "LEFT JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:from is null or c.updated < :from) " +
            "AND (:domainIds is null or c.domainId IN (:domainIds)) " +
            "GROUP BY case when d.name is null then 'Super domain' else d.name end, m.name, p.model")
    List<ProductGroupWithCountProjection> getDeviceOfflineReport(List<Integer> domainIds, String manufacturer, String model,
                                                                 Instant from);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end as domain, c.id as deviceId, " +
            "c.created as created, c.serial as serial, m.name as manufacturer, p.model as model, " +
            "c.created as updated " +
            "FROM CpeEntity c " +
            "LEFT JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:from is null or c.updated < :from) " +
            "AND (:isIdsNull is true or c.domainId IN :domainIds)")
    List<Object[]> getDeviceOfflineReportExcel(List<Integer> domainIds, boolean isIdsNull, String manufacturer, String model,
                                                          Instant from);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end as domainName, " +
            "l.activityType as activityType, count(l.id) as count " +
            "FROM CpeEntity c " +
            "INNER JOIN DeviceHistoryEntity l ON l.deviceId = c.id " +
            "LEFT JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:from is null or l.created >= :from) AND (:to is null or l.created <= :to) " +
            "AND (:domainIds is null or c.domainId IN :domainIds) " +
            "AND (:activityType is null or l.activityType = :activityType) " +
            "GROUP BY case when d.name is null then 'Super domain' else d.name end, l.activityType " +
            "HAVING count(l.id) >= :minCount")
    List<ProductGroupEventReportProjection> getDeviceEventReport(List<Integer> domainIds, String manufacturer, String model,
                                                                 Instant from, Instant to, String activityType, Long minCount);

    @Query("SELECT case when d.name is null then 'Super domain' else d.name end as domain, " +
            "l.activityType as activity_type, c.id as deviceId, c.created as created, c.serial as serial, " +
            "m.name as manufacturer, p.model as model, l.created as last_session, count(l.id) as count " +
            "FROM CpeEntity c " +
            "INNER JOIN DeviceHistoryEntity l ON l.deviceId = c.id " +
            "LEFT JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "LEFT JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "WHERE (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:from is null or l.created >= :from) AND (:to is null or l.created <= :to) " +
            "AND (:isIdsNull is true or c.domainId IN :domainIds) " +
            "AND (:activityType is null or l.activityType = :activityType) " +
            "GROUP BY case when d.name is null then 'Super domain' else d.name end, l.activityType, c.id, c.created, " +
            "c.serial, m.name, p.model, l.created " +
            "HAVING count(l.id) >= :minCount")
    List<Object[]> getDeviceEventReportExcel(List<Integer> domainIds, boolean isIdsNull, String manufacturer,
                                                        String model, Instant from, Instant to, String activityType,
                                                        Integer minCount);

    @Query("SELECT m.name as manufacturerName, p.model as model, c.firmware as firmware, " +
            "case when d.name is null then 'Super domain' else d.name end as domainName, count(c.id) as count " +
            "FROM CpeEntity c " +
            "INNER JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "INNER JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "WHERE c.firmware is not null " +
            "AND (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:domainIds is null or c.domainId IN :domainIds) " +
            "GROUP BY m.name, p.model, c.firmware, case when d.name is null then 'Super domain' else d.name end " +
            "ORDER BY m.name, p.model, c.firmware")
    Page<ProductGroupFirmwareReportProjection> getFirmwareVersionReport(List<Integer> domainIds, String manufacturer, String model,
                                                                        Pageable pageable);

    @Query("SELECT m.name as manufacturer, p.model as model, c.firmware as firmware, " +
            "case when d.name is null then 'Super domain' else d.name end as domain, count(c.id) as count " +
            "FROM CpeEntity c " +
            "INNER JOIN ProductClassEntity p ON c.productClassId = p.id " +
            "INNER JOIN ManufacturerEntity m ON p.manufId = m.id " +
            "LEFT JOIN DomainEntity d ON c.domainId = d.id " +
            "WHERE c.firmware is not null " +
            "AND (:manufacturer is null or m.name = :manufacturer) " +
            "AND (:model is null or p.model = :model) " +
            "AND (:isIdsNull is true or c.domainId IN :domainIds) " +
            "GROUP BY m.name, p.model, c.firmware, case when d.name is null then 'Super domain' else d.name end " +
            "ORDER BY m.name, p.model, c.firmware")
    List<Object[]> getFirmwareVersionReport(List<Integer> domainIds, boolean isIdsNull, String manufacturer, String model);


    @Query(nativeQuery = true,
            value = "SELECT DISTINCT location_id " +
                    "FROM cpe")
    List<Integer> getAllDomainIdsWithDevices();


    @Query(nativeQuery = true,
            value = "SELECT count(*) FROM cpe_next_session_time where cpe_id=:deviceId and next_session_time >=:ts")
    Integer checkCpeInNextSession(Instant ts, final Long deviceId);

}
