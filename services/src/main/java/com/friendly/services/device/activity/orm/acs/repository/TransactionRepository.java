package com.friendly.services.device.activity.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseReadOnlyJpaRepository;
import com.friendly.services.device.activity.orm.acs.model.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link TransactionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface TransactionRepository extends BaseReadOnlyJpaRepository<TransactionEntity, Integer> {
    @Query("select t.creator from TransactionEntity t where t.id = :id")
    String getCreatorFromTransaction(Long id);

    @Query(nativeQuery = true, value = "SELECT id " +
            "FROM ftacs.transaction " +
            "WHERE creator = ?1 " +
            "AND created BETWEEN ?2 AND ?3 " +
            "ORDER BY created DESC;")
    List<Integer> getTransactionIdsInRange(String creator, Instant startDate, Instant endDate);
}
