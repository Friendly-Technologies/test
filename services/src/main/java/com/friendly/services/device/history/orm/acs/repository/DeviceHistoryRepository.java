package com.friendly.services.device.history.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.history.orm.acs.model.DeviceHistoryEntity;
import com.friendly.services.device.history.orm.acs.model.DeviceHistoryDetailsProjection;
import java.time.Instant;
import java.util.List;

import com.friendly.services.device.history.orm.acs.model.DeviceHistoryProjection;
import com.friendly.services.device.info.model.ProvHistoryDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository to interact with persistence layer to store {@link DeviceHistoryEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceHistoryRepository extends BaseJpaRepository<DeviceHistoryEntity, Long> {

    @Query("SELECT h FROM DeviceHistoryEntity h WHERE h.deviceId = :deviceId " +
            "AND (:activityType is null or h.activityType = :activityType) " +
            "AND (:from is null or (h.created > :from and h.created < :to)) ")
    Page<DeviceHistoryEntity> getDeviceHistory(Long deviceId, String activityType,
                                               Instant from, Instant to, Pageable pageable);

    @Query(nativeQuery = true, value = "select clen.name as 'Activity type', h.created as 'Created', " +
            "cpn.name as 'Parameter name', ccp.previous_value as 'Old value' ,ccp.current_value as 'New value' " +
            "from cpe_log h " +
            "    left join cpe_changed_parameter ccp on ccp.cpe_log_id = h.id " +
            "    left join cpe_log_event_name clen on clen.id = h.event_code_id " +
            "    left join cpe_parameter_name cpn on cpn.id = ccp.name_id " +
            "where h.cpe_id = ?1 " +
            "    and h.created > ?2 " +
            "    and h.created < ?3")
    List<Object[]> getDeviceHistory(Long deviceId, Instant from, Instant to);

    @Query(nativeQuery = true, value = "SELECT DISTINCT en.name FROM cpe_log_event_name en " +
            "left join cpe_log l on l.event_code_id = en.id WHERE ?1 is null or l.cpe_id = ?1 ORDER BY en.name")
    List<String> getDeviceHistoryActivityTypes(final Long deviceId);

    @Query(
            nativeQuery = true,
            value = "SELECT count(*) FROM cpe_changed_parameter a WHERE a.cpe_log_id = ?1")
    Integer getItemsCount(Long logId);

    boolean existsByIdAndDeviceId(final Long id, final Long deviceId);

    @Transactional
    @Modifying
    int deleteByIdAndDeviceId(final Long id, final Long deviceId);

    @Query(nativeQuery = true,
            value = "SELECT a.previous_value as prevValue, a.current_value as curValue, a.created as created, a.name_id as nameId " +
                    "FROM cpe_changed_parameter a\n" +
                    "where a.cpe_log_id = ?1\n" +
                    "order by id;")
    List<DeviceHistoryDetailsProjection> getDeviceHistoryDetails(Integer id);

    @Query(nativeQuery = true, value = "SELECT count(*) FROM cpe_changed_parameter cp " +
            "inner join cpe_log l on l.id = cp.cpe_log_id " +
            "inner join cpe_parameter_name n on n.id = cp.name_id " +
            "inner join cpe_log_event_name en on l.event_code_id = en.id " +
            "where l.cpe_id = ?1 and l.created >= ?2 " +
            "and n.name like '%ExternalIPAddress%' " +
            "and en.name = '4 VALUE CHANGE'")
    Long getConnectivityFailureCount(final Long deviceId, final Instant from);

    @Query(nativeQuery = true, value = "SELECT count(*) " +
            "FROM cpe_log l " +
            "INNER JOIN cpe_log_event_name en " +
            "ON l.event_code_id = en.id " +
            "WHERE l.cpe_id = ?1 " +
            "AND l.created >= ?2 " +
            "AND en.name = '1 BOOT'")
    Long getRebootAmount(final Long deviceId, final Instant from);


    @Query(nativeQuery = true, value = "SELECT ph.name_id as nameId, ph.value as value from cpe_provision_history ph where ph.task_id = :taskId")
    List<ProvHistoryDetailProjection> getValueAndNameIdFromProvHistory(Long taskId);
    @Query(
            nativeQuery = true,
            value =
                    "SELECT count(*)\n"
                            + "FROM cpe_file_history h, cpe_pending_task t\n"
                            + "WHERE t.id = h.task_id\n"
                            + "  AND t.type_id = 29\n"
                            + "  ANd h.file_type_id = 1\n"
                            + "  AND t.cpe_id = ?1 ")
    Integer checkIfFirmwareHasUpdate(Long id);

    @Query(nativeQuery = true, value =
            "SELECT date( DATE_ADD( created, INTERVAL :diffMinutes MINUTE)) AS created, "
                    + "event_code_id AS eventCodeId, count(*) AS count "
                    + "FROM cpe_log where cpe_id=:cpeId "
                    + "GROUP BY date( DATE_ADD( created, INTERVAL :diffMinutes MINUTE)), event_code_id "
                    + "ORDER BY 1, 2")
    List<DeviceHistoryProjection> findGroupedDataByCpeId(Long cpeId, Long diffMinutes);

  @Query(nativeQuery = true,
          value = "SELECT id FROM cpe_log_event_name WHERE name = ?1")
  List<Integer> getEventCodeIdsByName(String name);
}
