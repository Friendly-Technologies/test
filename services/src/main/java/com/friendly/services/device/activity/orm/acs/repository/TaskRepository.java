package com.friendly.services.device.activity.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.activity.orm.acs.model.CpeCompletedTaskEntity;
import com.friendly.services.device.activity.orm.acs.model.CpeFailedTaskEntity;
import com.friendly.services.device.activity.orm.acs.model.CpePendingTaskEntity;
import com.friendly.services.device.activity.orm.acs.model.CpeRejectedTaskEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link CpeCompletedTaskEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface TaskRepository extends BaseJpaRepository<CpeCompletedTaskEntity, Long>,
                                        PagingAndSortingRepository<CpeCompletedTaskEntity, Long> {

    @Query(nativeQuery = true, value = "select DISTINCT t.task_name FROM " +
            "(SELECT DISTINCT task_name FROM cpe_completed_task where cpe_id = ?1 " +
            "union SELECT DISTINCT task_name FROM cpe_rejected_task where cpe_id = ?1 " +
            "union SELECT DISTINCT task_name FROM cpe_failed_task where cpe_id = ?1 " +
            "union SELECT DISTINCT task_name FROM cpe_pending_task where cpe_id = ?1) t")
    List<String> getDeviceActivityTaskNames(Long cpeId);

    @Query(nativeQuery = true, value = "select state, id, task_name, created, completed, transaction_id, " +
            "type_id, task_key, fault_code, description FROM " +
            "(SELECT 'Completed' as state, id, task_name, created, completed, transaction_id, type_id, task_key, " +
            "null as fault_code, null as description " +
            "FROM cpe_completed_task where cpe_id = ?1 and completed is not null " +
            "and (coalesce(?8, null) is null or id in (?8)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            "union SELECT 'Rejected' as state, r.id, r.task_name, r.created as created, l.created as completed, r.transaction_id, r.type_id, r.task_key, " +
            "l.fault_code, en.description " +
            "FROM cpe_rejected_task r left join error_log l on l.task_id = r.id " +
            "and (coalesce(?8, null) is null or r.id in (?8)) " +
            "left join error_log_errortext_name en on l.error_text_id = en.id where r.cpe_id = ?1 " +
            "and (?2 is null or r.task_name like ?2) and (?3 is null or r.created > ?3) and (?4 is null or r.created < ?4) " +
            "union SELECT 'Failed' as state, f.id, f.task_name, f.created as created, l.created as completed, f.transaction_id, f.type_id, f.task_key, " +
            "l.fault_code, en.description " +
            "FROM cpe_failed_task f left join error_log l on l.task_id = f.id " +
            "left join error_log_errortext_name en on l.error_text_id = en.id where f.cpe_id = ?1 " +
            "and (coalesce(?8, null) is null or f.id in (?8)) " +
            "and (?2 is null or f.task_name like ?2) and (?3 is null or f.created > ?3) and (?4 is null or f.created < ?4) " +
            "union SELECT 'Pending' as state, id, task_name, created, null, transaction_id, type_id, task_key, " +
            "null as fault_code, null as description " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats=0 " +
            "and (coalesce(?8, null) is null or id in (?8)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            "union SELECT 'Sent' as state, id, task_name, created, null, transaction_id, type_id, task_key, " +
            "null as fault_code, null as description " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats>0 " +
            "and (coalesce(?8, null) is null or id in (?8)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            ") t ORDER BY case " +
            "when ?7 = 'id' then id " +
            "when ?7 = 'state' then state " +
            "when ?7 = 'task_name' then task_name " +
            "when ?7 = 'created' then created " +
            "when ?7 = 'completed' then completed " +
            "when ?7 = 'fault_code' then fault_code " +
            "when ?7 = 'description' then description " +
            "else id end ASC, id asc limit ?5, ?6")
    List<Object[]> getDeviceActivityASC(Long cpeId, String taskName, Instant from, Instant to,
                                        int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value = "select state, id, task_name, created, completed, transaction_id, " +
            "type_id, task_key, fault_code, description FROM " +
            "(SELECT 'Completed' as state, id, task_name, created, completed, transaction_id, type_id, task_key, " +
            "null as fault_code, null as description " +
            "FROM cpe_completed_task where cpe_id = ?1 and completed is not null " +
            "and (coalesce(?8, null) is null or id in (?8)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            "union SELECT 'Rejected' as state, r.id, r.task_name, r.created as created, l.created as completed, r.transaction_id, r.type_id, r.task_key, " +
            "l.fault_code, en.description " +
            "FROM cpe_rejected_task r left join error_log l on l.task_id = r.id " +
            "and (coalesce(?8, null) is null or r.id in (?8)) " +
            "left join error_log_errortext_name en on l.error_text_id = en.id where r.cpe_id = ?1 " +
            "and (?2 is null or r.task_name like ?2) and (?3 is null or r.created > ?3) and (?4 is null or r.created < ?4) " +
            "union SELECT 'Failed' as state, f.id, f.task_name, f.created as created, l.created as completed, f.transaction_id, f.type_id, f.task_key, " +
            "l.fault_code, en.description " +
            "FROM cpe_failed_task f left join error_log l on l.task_id = f.id " +
            "and (coalesce(?8, null) is null or f.id in (?8)) " +
            "left join error_log_errortext_name en on l.error_text_id = en.id where f.cpe_id = ?1 " +
            "and (?2 is null or f.task_name like ?2) and (?3 is null or f.created > ?3) and (?4 is null or f.created < ?4) " +
            "union SELECT 'Pending' as state, id, task_name, created, null, transaction_id, type_id, task_key, " +
            "null as fault_code, null as description " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats=0 " +
            "and (coalesce(?8, null) is null or id in (?8)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            "union SELECT 'Sent' as state, id, task_name, created, null, transaction_id, type_id, task_key, " +
            "null as fault_code, null as description " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats>0 " +
            "and (coalesce(?8, null) is null or id in (?8)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            ") t ORDER BY case " +
            "when ?7 = 'id' then id " +
            "when ?7 = 'state' then state " +
            "when ?7 = 'task_name' then task_name " +
            "when ?7 = 'created' then created " +
            "when ?7 = 'completed' then completed " +
            "when ?7 = 'fault_code' then fault_code " +
            "when ?7 = 'description' then description " +
            "else id end DESC, id DESC limit ?5, ?6")
    List<Object[]> getDeviceActivityDESC(Long cpeId, String taskName, Instant from, Instant to,
                                         int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value = "select state, task_name, task_key FROM " +
            "(SELECT 'Completed' as state, task_name, task_key " +
            "FROM cpe_completed_task where cpe_id = ?1 and type_id = ?2 and completed is not null " +
            "union SELECT 'Rejected' as state, task_name, task_key " +
            "FROM cpe_rejected_task where cpe_id = ?1 and type_id = ?2 " +
            "union SELECT 'Failed' as state, task_name, task_key " +
            "FROM cpe_failed_task  where cpe_id = ?1 and type_id = ?2  " +
            "union SELECT 'Pending' as state, task_name, task_key " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats=0 and type_id = ?2 " +
            "union SELECT 'Sent' as state, task_name, task_key " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats>0 and type_id = ?2 " +
            ") t")
    List<Object[]> getDeviceActivityForType(Long cpeId, Integer typeId);

    @Query(nativeQuery = true, value =
            "(SELECT 'Sent' as state, id, task_name, created, CAST(null AS CHAR), transaction_id, type_id, task_key " +
                    "FROM cpe_pending_task where cpe_id = ?1 and repeats>0 " +
                    "and (coalesce(?8, null) is null or id in (?8)) " +
                    "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then id " +
                    "when ?7 = 'task_name' then task_name " +
                    "when ?7 = 'created' then created " +
                    "else id end DESC limit ?5, ?6)")
    List<Object[]> getDeviceActivitySentDESC(Long cpeId, String taskName, Instant from, Instant to,
                                                int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value =
            "(SELECT 'Sent' as state, id, task_name, created, CAST(null AS CHAR), transaction_id, type_id, task_key " +
                    "FROM cpe_pending_task where cpe_id = ?1 and repeats>0 " +
                    "and (coalesce(?8, null) is null or id in (?8)) " +
                    "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then id " +
                    "when ?7 = 'task_name' then task_name " +
                    "when ?7 = 'created' then created " +
                    "else id end, id asc limit ?5, ?6)")
    List<Object[]> getDeviceActivitySentASC(Long cpeId, String taskName, Instant from, Instant to,
                                               int skip, int limit, String orderField, List<Long> ids);


    @Query(nativeQuery = true, value =
            "(SELECT 'Pending' as state, id, task_name, created, CAST(null AS CHAR), transaction_id, type_id, task_key " +
                    "FROM cpe_pending_task where cpe_id = ?1 and repeats=0 " +
                    "and (coalesce(?8, null) is null or id in (?8)) " +
                    "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then id " +
                    "when ?7 = 'task_name' then task_name " +
                    "when ?7 = 'created' then created " +
                    "else id end DESC, id desc limit ?5, ?6)")
    List<Object[]> getDeviceActivityPendingDESC(Long cpeId, String taskName, Instant from, Instant to,
                                                 int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value =
            "(SELECT 'Pending' as state, id, task_name, created, CAST(null AS CHAR), transaction_id, type_id, task_key " +
                    "FROM cpe_pending_task where cpe_id = ?1 and repeats=0 " +
                    "and (coalesce(?8, null) is null or id in (?8)) " +
                    "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then id " +
                    "when ?7 = 'task_name' then task_name " +
                    "when ?7 = 'created' then created " +
                    "else id end, id asc limit ?5, ?6)")
    List<Object[]> getDeviceActivityPendingASC(Long cpeId, String taskName, Instant from, Instant to,
                                                int skip, int limit, String orderField, List<Long> ids);


    @Query(nativeQuery = true, value =
            "(SELECT 'Rejected' as state, r.id, r.task_name, r.created as created, l.created as completed, r.transaction_id, r.type_id, r.task_key, " +
                    "l.fault_code, en.description " +
                    "FROM cpe_rejected_task r left join error_log l on l.task_id = r.id " +
                    "and (coalesce(?8, null) is null or r.id in (?8)) " +
                    "left join error_log_errortext_name en on l.error_text_id = en.id where r.cpe_id = ?1 " +
                    "and (?2 is null or r.task_name like ?2) and (?3 is null or r.created > ?3) and (?4 is null or r.created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then r.id " +
                    "when ?7 = 'task_name' then r.task_name " +
                    "when ?7 = 'created' then r.created " +
                    "else r.id end DESC, id desc limit ?5, ?6)")
    List<Object[]> getDeviceActivityRejectedDESC(Long cpeId, String taskName, Instant from, Instant to,
                                                int skip, int limit, String orderField, List<Long> ids);


    @Query(nativeQuery = true, value =
            "(SELECT 'Rejected' as state, r.id, r.task_name, r.created as created, l.created as completed, r.transaction_id, r.type_id, r.task_key, " +
                    "l.fault_code, en.description " +
                    "FROM cpe_rejected_task r left join error_log l on l.task_id = r.id " +
                    "and (coalesce(?8, null) is null or r.id in (?8)) " +
                    "left join error_log_errortext_name en on l.error_text_id = en.id where r.cpe_id = ?1 " +
                    "and (?2 is null or r.task_name like ?2) and (?3 is null or r.created > ?3) and (?4 is null or r.created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then r.id " +
                    "when ?7 = 'task_name' then r.task_name " +
                    "when ?7 = 'created' then r.created " +
                    "else r.id end, id asc limit ?5, ?6)")
    List<Object[]> getDeviceActivityRejectedASC(Long cpeId, String taskName, Instant from, Instant to,
                                              int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value =
            "(SELECT 'Failed' as state, f.id, f.task_name, f.created as created,l.created as completed, f.transaction_id, f.type_id, f.task_key, " +
                    "l.fault_code, en.description " +
                    "FROM cpe_failed_task f left join error_log l on l.task_id = f.id " +
                    "and (coalesce(?8, null) is null or f.id in (?8)) " +
                    "left join error_log_errortext_name en on l.error_text_id = en.id where f.cpe_id = ?1 " +
                    "and (?2 is null or f.task_name like ?2) and (?3 is null or f.created > ?3) and (?4 is null or f.created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then f.id " +
                    "when ?7 = 'task_name' then f.task_name " +
                    "when ?7 = 'created' then f.created " +
                    "else f.id end DESC, id desc limit ?5, ?6)")
    List<Object[]> getDeviceActivityFailedDESC(Long cpeId, String taskName, Instant from, Instant to,
                                         int skip, int limit, String orderField, List<Long> ids);


    @Query(nativeQuery = true, value =
            "(SELECT 'Failed' as state, f.id, f.task_name, f.created as created, l.created as completed, f.transaction_id, f.type_id, f.task_key, " +
                    "l.fault_code, en.description " +
                    "FROM cpe_failed_task f left join error_log l on l.task_id = f.id " +
                    "and (coalesce(?8, null) is null or f.id in (?8)) " +
                    "left join error_log_errortext_name en on l.error_text_id = en.id where f.cpe_id = ?1 " +
                    "and (?2 is null or f.task_name like ?2) and (?3 is null or f.created > ?3) and (?4 is null or f.created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then f.id " +
                    "when ?7 = 'task_name' then f.task_name " +
                    "when ?7 = 'created' then f.created " +
                    "else f.id end, id asc limit ?5, ?6)")
    List<Object[]> getDeviceActivityFailedASC(Long cpeId, String taskName, Instant from, Instant to,
                                               int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value =
            "(SELECT 'Completed' as state, id, task_name, created, completed, transaction_id, type_id, task_key " +
            "FROM cpe_completed_task where cpe_id = ?1 and completed is not null " +
            "and (coalesce(?8, null) is null or id in (?8)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            "ORDER BY case " +
            "when ?7 = 'id' then id " +
            "when ?7 = 'state' then state " +
            "when ?7 = 'task_name' then task_name " +
            "when ?7 = 'created' then created " +
            "when ?7 = 'completed' then completed " +
            "else id end, id asc limit ?5, ?6)")
    List<Object[]> getDeviceActivityCompletedASC(Long cpeId, String taskName, Instant from, Instant to,
                                                  int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value =
            "(SELECT 'Completed' as state, id, task_name, created, completed, transaction_id, type_id, task_key " +
                    "FROM cpe_completed_task where cpe_id = ?1 and completed is not null " +
                    "and (coalesce(?8, null) is null or id in (?8)) " +
                    "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
                    "ORDER BY case " +
                    "when ?7 = 'id' then id " +
                    "when ?7 = 'state' then state " +
                    "when ?7 = 'task_name' then task_name " +
                    "when ?7 = 'created' then created " +
                    "when ?7 = 'completed' then completed " +
                    "else id end DESC, id desc limit ?5, ?6)")
    List<Object[]> getDeviceActivityCompletedDESC(Long cpeId, String taskName, Instant from, Instant to,
                                                  int skip, int limit, String orderField, List<Long> ids);

    @Query(nativeQuery = true, value = "(SELECT count(id) as c FROM cpe_completed_task where cpe_id = ?1 and completed is not null " +
            "and (coalesce(?5, null) is null or id in (?5)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4))")
    Long getDeviceActivityCompletedCount(Long cpeId, String taskName, Instant from, Instant to, List<Long> ids);

    @Query(nativeQuery = true, value = "SELECT count(id) as c FROM cpe_failed_task where cpe_id = ?1 " +
            "and (coalesce(?5, null) is null or id in (?5)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4)")
    Long getDeviceActivityFailedCount(Long cpeId, String taskName, Instant from, Instant to, List<Long> ids);

    @Query(nativeQuery = true, value = "SELECT count(id) as c FROM cpe_rejected_task where cpe_id = ?1 " +
            "and (coalesce(?5, null) is null or id in (?5)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4)")
    Long getDeviceActivityRejectedCount(Long cpeId, String taskName, Instant from, Instant to, List<Long> ids);

    @Query(nativeQuery = true, value = "SELECT count(id) as c FROM cpe_pending_task where cpe_id = ?1 " +
            "and (coalesce(?5, null) is null or id in (?5)) and repeats='0'" +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4)")
    Long getDeviceActivityPendingCount(Long cpeId, String taskName, Instant from, Instant to, List<Long> ids);

    @Query(nativeQuery = true, value = "SELECT count(id) as c FROM cpe_pending_task where cpe_id = ?1 " +
            "and (coalesce(?5, null) is null or id in (?5)) and repeats > '0'" +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4)")
    Long getDeviceActivitySentCount(Long cpeId, String taskName, Instant from, Instant to, List<Long> ids);


    @Query("select c FROM CpePendingTaskEntity c where c.transactionId = :transactionId")
    List<CpePendingTaskEntity> getPendingTasksByTransactionId(Long transactionId);

    @Query("select c FROM CpeCompletedTaskEntity c where c.transactionId = :transactionId")
    List<CpeCompletedTaskEntity> getCompletedTasksByTransactionId(Long transactionId);

    @Query("select c FROM CpeRejectedTaskEntity c where c.transactionId = :transactionId")
    List<CpeRejectedTaskEntity> getRejectedTasksByTransactionId(Long transactionId);

    @Query("select c FROM CpeFailedTaskEntity c where c.transactionId = :transactionId")
    List<CpeFailedTaskEntity> getFailedTasksByTransactionId(Long transactionId);

    @Query("select c.id FROM CpePendingTaskEntity c where c.transactionId IN :transactionIds and c.repeats=0 " +
            "AND c.cpeId = :deviceId")
    List<Long> getPendingTaskIdsByTransactionIdAndDeviceId(List<Long> transactionIds, Long deviceId);

    @Query("select c.id FROM CpePendingTaskEntity c where c.transactionId IN :transactionIds and c.repeats>0 " +
            "AND c.cpeId = :deviceId")
    List<Long> getSentTaskIdsByTransactionIdAndDeviceId(List<Long> transactionIds, Long deviceId);

    @Query("select c.id FROM CpeCompletedTaskEntity c where c.transactionId IN :transactionIds " +
            "AND c.cpeId = :deviceId")
    List<Long> getCompletedTaskIdsByTransactionIdAndDeviceId(List<Long> transactionIds, Long deviceId);

    @Query("select c.id FROM CpeRejectedTaskEntity c where c.transactionId IN :transactionIds " +
            "AND c.cpeId = :deviceId")
    List<Long> getRejectedTaskIdsByTransactionIdAndDeviceId(List<Long> transactionIds, Long deviceId);

    @Query("select c.id FROM CpeFailedTaskEntity c where c.transactionId IN :transactionIds " +
            "AND c.cpeId = :deviceId")
    List<Long> getFailedTaskIdsByTransactionIdAndDeviceId(List<Long> transactionIds, Long deviceId);


    @Query(nativeQuery = true, value = "select sum(t.c) FROM " +
            "(SELECT count(id) as c FROM cpe_completed_task where cpe_id = ?1 and completed is not null " +
            "and (coalesce(?5, null) is null or id in (?5)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            "union SELECT count(id) as c FROM cpe_rejected_task where cpe_id = ?1 " +
            "and (coalesce(?5, null) is null or id in (?5)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) " +
            "union SELECT count(id) as c FROM cpe_failed_task where cpe_id = ?1 " +
            "and (coalesce(?5, null) is null or id in (?5)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4)) t")
    Long getDeviceActivityCount(Long cpeId, String taskName, Instant from, Instant to, List<Long> ids);

    @Query(nativeQuery = true, value = "SELECT count(id) as c FROM cpe_pending_task where cpe_id = ?1 " +
            "and (coalesce(?5, null) is null or id in (?5)) " +
            "and (?2 is null or task_name like ?2) and (?3 is null or created > ?3) and (?4 is null or created < ?4) ")
    Long getAllDeviceActivityPendingCount(Long cpeId, String taskName, Instant from, Instant to, List<Long> ids);

    @Query("SELECT count(distinct ct.id), count(distinct ft.id), count(distinct rt.id), " +
            "count(distinct case when pt.repeats = 0 then pt.id else null end), " +
            "count(distinct case when pt.repeats = 0 then null else pt.id end) " +
            "FROM CpeEntity c " +
            "LEFT JOIN CpeCompletedTaskEntity ct ON c.id = ct.cpeId " +
            "LEFT JOIN CpeFailedTaskEntity ft ON c.id = ft.cpeId " +
            "LEFT JOIN CpeRejectedTaskEntity rt ON c.id = rt.cpeId " +
            "LEFT JOIN CpePendingTaskEntity pt ON c.id = pt.cpeId " +
            "WHERE c.id = :deviceId")
    List<Object[]> getTasks(final Long deviceId);

    @Query(nativeQuery = true, value = "select count(id)  FROM cpe_pending_task where cpe_id = ?1 and confirmed=0 and repeats=0")
    Integer getPendingTaskCount(final Long deviceId);
    @Query(nativeQuery = true, value = "select count(id)  FROM cpe_pending_task where cpe_id = ?1 and (confirmed=1 or repeats>0)")
    Integer getSentTaskCount(final Long deviceId);
    @Query(nativeQuery = true, value = "select count(id)  FROM cpe_completed_task where cpe_id = ?1")
    Integer getCompletedTaskCount(final Long deviceId);
    @Query(nativeQuery = true, value = "select count(id)  FROM cpe_rejected_task where cpe_id = ?1")
    Integer getRejectedTaskCount(final Long deviceId);
    @Query(nativeQuery = true, value = "select count(id)  FROM cpe_failed_task where cpe_id = ?1")
    Integer getFailedTaskCount(final Long deviceId);

    @Query(nativeQuery = true, value = "SELECT count(l.id) " +
            "FROM cpe_completed_task l  " +
            "WHERE l.cpe_id = :deviceId " +
            "AND l.completed >= :from " +
            "AND l.type_id = :typeId")
    Long getCompletedTasksOfTypeAmount(final Long deviceId, final Instant from, final Integer typeId);

    @Query(nativeQuery = true, value = "select exists " +
            "(SELECT id as c FROM cpe_completed_task where cpe_id = ?1 " +
            "union SELECT id as c FROM cpe_rejected_task where cpe_id = ?1 " +
            "union SELECT id as c FROM cpe_failed_task where cpe_id = ?1 " +
            "union SELECT id as c FROM cpe_pending_task where cpe_id = ?1 ) t")
    BigInteger isActivityExist(Long deviceId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpeCompletedTaskEntity t WHERE t.cpeId = :cpeId and t.id = :id")
    int deleteCompletedTask(Long cpeId, Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpePendingTaskEntity t WHERE t.cpeId = :cpeId and t.id = :id")
    int deletePendingTask(Long cpeId, Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpeRejectedTaskEntity t WHERE t.cpeId = :cpeId and t.id = :id")
    int deleteRejectedTask(Long cpeId, Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpeFailedTaskEntity t WHERE t.cpeId = :cpeId and t.id = :id")
    int deleteFailedTask(Long cpeId, Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpePendingTaskEntity t WHERE t.cpeId = :cpeId and t.typeId in (15, 16) " +
            "and t.taskKey = :diagnosticId")
    int deleteDiagnosticFromPendingTask(Long cpeId, Long diagnosticId);


    @Query(nativeQuery = true,
            value = "select count(distinct cpe_id) from ( " +
                    "select distinct cpe_id from cpe_pending_task where transaction_id IN ?1 " +
                    "union " +
                    "select distinct cpe_id from cpe_completed_task where transaction_id IN ?1 " +
                    "union " +
                    "select distinct cpe_id from cpe_failed_task where transaction_id IN ?1 " +
                    "union " +
                    "select distinct cpe_id from cpe_rejected_task where transaction_id IN ?1) as derived_table_alias"
    )
    Integer getCpeIdsFromTransactionIds(List<Integer> transactionIds);

}
