package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupTransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link UpdateGroupTransactionEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface UpdateGroupTransactionRepository extends BaseJpaRepository<UpdateGroupTransactionEntity, Integer> {

    @Query(nativeQuery = true,
            value = "select IFNULL" +
                    "((select max(ugt.created) " +
                    "from  ug_transaction ugt " +
                    "where ugt.ug_id=u.id), u.scheduled) "+
            "FROM  update_group u " +
            "where u.id=?1 ")
    Instant getActivatedDate(Integer id);

    @Query(nativeQuery = true,
            value = "select NVL" +
                    "((select max(ugt.created) " +
                    "from  ug_transaction ugt " +
                    "where ugt.ug_id=u.id), u.scheduled) "+
                    "FROM  update_group u " +
                    "where u.id=?1 ")
    Timestamp getActivatedDateOracle(Integer id);

    List<UpdateGroupTransactionEntity> findAllByUpdateGroupId(Integer updateGroupId);
}
