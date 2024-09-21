package com.friendly.services.device.diagnostics.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.diagnostics.orm.acs.model.DeviceDiagnosticsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Repository to interact with persistence layer to store {@link DeviceDiagnosticsEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceDiagnosticsRepository extends BaseJpaRepository<DeviceDiagnosticsEntity, Long>,
                                                     PagingAndSortingRepository<DeviceDiagnosticsEntity, Long> {

    Page<DeviceDiagnosticsEntity> findAllByDeviceId(final Long deviceId, final Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT d.value FROM cpe_get_diagnostic d " +
            "inner join cpe_parameter_name n on d.name_id = n.id " +
            "where d.cpe_diagnostic_id = ?1 and n.name like ?2 ")
    List<String> getDiagnosticGetParams(Long diagnosticId, String paramName);

    @Query(nativeQuery = true, value = "SELECT d.value FROM cpe_set_diagnostic d " +
            "inner join cpe_parameter_name n on d.name_id = n.id " +
            "where d.cpe_diagnostic_id = ?1 and n.name like ?2 ")
    List<String> getDiagnosticSetParams(Long diagnosticId, String paramName);

    @Query(nativeQuery = true, value = "SELECT n.name as name, d.value as value FROM cpe_get_diagnostic d " +
            "inner join cpe_parameter_name n on d.name_id = n.id " +
            "where d.cpe_diagnostic_id = :diagnosticId and n.name like :resultFilter ")
    List<Map<String, String>> getDiagnosticResults(Long diagnosticId, String resultFilter);

    /**
     * Retrieves number and parameter name, appending 'Hop' where needed, with corresponding values, filtered
     * by diagnostic ID and RouteHops.
     */
    @Query(nativeQuery = true, value =
            "SELECT " +
            "    SUBSTRING_INDEX(SUBSTRING_INDEX(n.name, 'RouteHops.', -1), '.', 1) AS number, " +
            "    CASE " +
            "        WHEN SUBSTRING_INDEX(n.name, '.', -1) LIKE 'Hop%' " +
            "            THEN SUBSTRING_INDEX(n.name, '.', -1) " +
            "        ELSE CONCAT('Hop', SUBSTRING_INDEX(n.name, '.', -1)) " +
            "        END AS name, " +
            "    d.value AS value " +
            "FROM cpe_get_diagnostic d " +
            "LEFT JOIN cpe_parameter_name n ON d.name_id = n.id " +
            "WHERE d.cpe_diagnostic_id = :diagnosticId " +
            "  AND n.name LIKE '%RouteHops.%' " +
            "  AND SUBSTRING_INDEX(n.name, '.', -1) <> ''")
    List<Object[]> getTraceDiagnosticResultsMysql(Long diagnosticId);

    @Query(nativeQuery = true, value =
            "SELECT " +
            "    SUBSTR(n.name, INSTR(n.name, '.', -1, 2) + 1, INSTR(n.name, '.', -1, 1) - INSTR(n.name, '.', -1, 2) - 1) AS number, " +
            "    CASE " +
            "        WHEN SUBSTR(n.name, INSTR(n.name, '.', -1, 1) + 1) LIKE 'Hop%' " +
            "            THEN SUBSTR(n.name, INSTR(n.name, '.', -1, 1) + 1) " +
            "        ELSE 'Hop' || SUBSTR(n.name, INSTR(n.name, '.', -1, 1) + 1) " +
            "    END AS name, " +
            "    d.value AS value " +
            "FROM cpe_get_diagnostic d " +
            "LEFT JOIN cpe_parameter_name n ON d.name_id = n.id " +
            "WHERE d.cpe_diagnostic_id = :diagnosticId " +
            "    AND n.name LIKE '%RouteHops.%' " +
            "    AND SUBSTR(n.name, INSTR(n.name, '.', -1) + 1) <> ''")
    List<Object[]> getTraceDiagnosticResultsOracle(Long diagnosticId);
    
    @Query(nativeQuery = true, value = "select case when m.name is null or m.name = '' then 'Diagnostics' else m.name end " +
            "from cpe_diagnostic m where m.id=?1")
    String getTaskNameFromCpeDiagnostic(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM DeviceDiagnosticsEntity d WHERE d.id = :id and d.deviceId = :deviceId")
    int deleteByIdAndDeviceId(final Long id, final Long deviceId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM cpe_get_diagnostic WHERE cpe_diagnostic_id = ?1")
    int deleteGetDiagnostic(Long diagnosticId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM cpe_set_diagnostic WHERE cpe_diagnostic_id = ?1")
    int deleteSetDiagnostic(Long diagnosticId);
}
