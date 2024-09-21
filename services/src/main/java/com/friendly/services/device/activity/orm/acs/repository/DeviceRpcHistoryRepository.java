package com.friendly.services.device.activity.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.activity.orm.acs.model.DeviceRpcHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Repository to interact with persistence layer to store {@link DeviceRpcHistoryEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceRpcHistoryRepository extends BaseJpaRepository<DeviceRpcHistoryEntity, Long>,
                                                    PagingAndSortingRepository<DeviceRpcHistoryEntity, Long> {

    @Modifying
    @Transactional
    int deleteByTaskId(final Long taskId);

    @Query(nativeQuery = true, value = "select t.id as id, state, completed, created, method_name, request_message as request, " +
            "response_message as response, method_name as method, creator as creator FROM " +
            "(SELECT 'Completed' as state, id, completed " +
            "FROM cpe_completed_task where cpe_id = ?1 and completed is not null and type_id = 37 " +
            "union SELECT 'Rejected' as state, id, null " +
            "FROM cpe_rejected_task where cpe_id = ?1 and type_id = 37 " +
            "union SELECT 'Failed' as state, id, null " +
            "FROM cpe_failed_task where cpe_id = ?1 and type_id = 37 " +
            "union SELECT 'Pending' as state, id, null " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats=0 and type_id = 37 " +
            "union SELECT 'Sent' as state, id, null " +
            "FROM cpe_pending_task where cpe_id = ?1 and repeats>0 and type_id = 37 " +
            ") t inner join custom_rpc_history r on t.id = r.task_id",
            countQuery = "SELECT count(t.id) FROM (SELECT 'Completed' as state, id, completed " +
                    "FROM cpe_completed_task where cpe_id = ?1 and completed is not null and type_id = 37 " +
                    "union SELECT 'Rejected' as state, id, null " +
                    "FROM cpe_rejected_task where cpe_id = ?1 and type_id = 37 " +
                    "union SELECT 'Failed' as state, id, null " +
                    "FROM cpe_failed_task where cpe_id = ?1 and type_id = 37 " +
                    "union SELECT 'Pending' as state, id, null " +
                    "FROM cpe_pending_task where cpe_id = ?1 and repeats=0 and type_id = 37 " +
                    "union SELECT 'Sent' as state, id, null " +
                    "FROM cpe_pending_task where cpe_id = ?1 and repeats>0 and type_id = 37 " +
                    ") t inner join custom_rpc_history r on t.id = r.task_id")
    Page<Map<String, Object>> getCustomRPCs(final Long deviceId, final Pageable pageable);

}
