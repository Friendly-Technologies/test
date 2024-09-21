package com.friendly.services.qoemonitoring.orm.qoe.repository;

import java.time.Instant;
import java.util.List;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.CpeDataEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.CpeDataEntityId;
import com.friendly.services.qoemonitoring.orm.qoe.model.projections.CpeDataProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CpeDataRepository extends BaseJpaRepository<CpeDataEntity, CpeDataEntityId> {
  @Query(nativeQuery = true, value =
      "SELECT DISTINCT c.serial as serial , c.name_id as nameId," +
              " c.created as created, c.value as value "
              + "FROM cpe_data c "
              + "WHERE c.serial = ?1 "
              + "AND c.created >= ?2 "
              + "AND c.name_id IN (?3) "
              + "order by 1")
  List<CpeDataProjection> getAllBySerialAndCreated(final String serial, final Instant date, final List<Integer> params);
}
