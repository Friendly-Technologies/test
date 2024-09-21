package com.friendly.services.qoemonitoring.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.acs.model.KpiEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiRepository extends BaseJpaRepository<KpiEntity, Long> {

  @Query(nativeQuery = true,
          value = "SELECT * FROM ftacs_qoe_ui.kpi k where k.kpi_name = ?1")
  KpiEntity findByKpiName(String name);

  @Query(nativeQuery = true,
          value = "SELECT k.kpi_name FROM ftacs_qoe_ui.kpi k where k.kpi_name IS NOT NULL ORDER BY 1")
  List<String> getFullNames();
}
