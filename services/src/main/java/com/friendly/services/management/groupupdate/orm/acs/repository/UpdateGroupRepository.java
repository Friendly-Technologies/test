package com.friendly.services.management.groupupdate.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.groupupdate.orm.acs.model.UpdateGroupEntity;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.UpdateGroupProjection;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link UpdateGroupEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface UpdateGroupRepository extends BaseJpaRepository<UpdateGroupEntity, Integer> {

    @Query("SELECT DISTINCT ug FROM UpdateGroupEntity ug " +
            "LEFT JOIN DomainEntity d ON ug.domainId = d.id " +
            "LEFT JOIN UpdateGroupChildEntity ugc ON ugc.parent = ug.id " +
            "WHERE (:manufacturer is null or ugc.productClass.manufacturerName = :manufacturer) " +
            "AND (:model is null or ugc.productClass.model = :model) " +
            "AND (:from is null or ug.updated >= :from) AND (:to is null or ug.updated <= :to) " +
            "AND (:domainIds is null or ug.domainId IN :domainIds) ")
    Page<UpdateGroupEntity> findAll(List<Integer> domainIds, String manufacturer, String model,
                                    Instant from, Instant to, Pageable pageable);

    @Query("SELECT DISTINCT ug FROM UpdateGroupEntity ug " +
            "LEFT JOIN UpdateGroupChildEntity ugc ON ugc.parent = ug.id " +
            "WHERE (:manufacturer is null or ugc.productClass.manufacturerName = :manufacturer) " +
            "AND (:model is null or ugc.productClass.model = :model) " +
            "AND (:state is null or :state = 8 or ug.state = :state)")
    Page<UpdateGroupEntity> findAll(String manufacturer, String model,
                                    Integer state, Pageable pageable);

    @Query("SELECT DISTINCT ugc.productClass.manufacturerName, ugc.productClass.model, ug.name, ug.created, " +
            "ug.creator, ug.domain, ug.updated, ug.scheduled, ug.state " +
            "FROM UpdateGroupEntity ug " +
            "LEFT JOIN DomainEntity d ON ug.domainId = d.id " +
            "LEFT JOIN UpdateGroupChildEntity ugc ON ugc.parent = ug.id " +
            "WHERE (:manufacturer is null or ugc.productClass.manufacturerName = :manufacturer) " +
            "AND (:model is null or ugc.productClass.model = :model) " +
            "AND (:from is null or ug.updated >= :from) AND (:to is null or ug.updated <= :to) " +
            "AND (:isIdsNull is true or ug.domainId IN :domainIds) ")
    List<Object[]> findAll(List<Integer> domainIds, boolean isIdsNull, String manufacturer, String model, Instant from, Instant to);


    @Query(nativeQuery = true,
            value = "SELECT d.id, d.serial " +
    "FROM cpe d, product_class p, manufacturer m " +
    "WHERE d.product_class_id = p.id " +
    "AND m.id = p.manuf_id " +
    "AND lower(p.model) = lower(?1) " +
    "AND lower(m.name) = lower(?2) " +
    "AND lower(d.serial) LIKE lower(?3)")
    List<Object[]> findAllDevicesForSelect(String modelName, String manufName,
                                                      String searchParam, Pageable p);

  @Query(
      nativeQuery = true,
      value =
          "SELECT d.id, c.serial\n"
              + "FROM cpe c, ug_cpe d left outer join\n"
              + "  (\n"
              + "    select cpe_id, state, updated\n"
              + "    from ug_cpe_completed  uc\n"
              + "    where uc.ug_id=?1 and ug_child_id=?2 \n"
              + "  ) uc on d.cpe_id=uc.cpe_id\n"
              + "where c.id=d.cpe_id and d.ug_id=?2\n"
              + "AND lower(d.serial) LIKE lower(?3)")
  List<Object[]> findIndividualDevicesForSelect(
      Long ugId, Long ugChildId, String searchParam, Pageable p);

  @Query(
      nativeQuery = true,
      value =
          "SELECT c.id id, c.serial "
              + "FROM ug_cpe_completed uc left outer join cpe c on c.id=uc.cpe_id "
              + "where uc.ug_id=?1 and ug_child_id=?2 " +
                  "AND lower(c.serial) LIKE lower(?3)")
  List<Object[]> findCompletedDevicesForSelect(Long ugId, Long ugChildId, String searchParam, Pageable p);

    @Query("SELECT DISTINCT ug.id as ugId, ugc.id as ugChildId, ug.state as state " +
            "FROM UpdateGroupEntity ug " +
            "LEFT JOIN UpdateGroupChildEntity ugc ON ugc.parent = ug.id " +
            "WHERE (:manufacturer is null or ugc.productClass.manufacturerName = :manufacturer) " +
            "AND (:model is null or ugc.productClass.model = :model)")
    List<UpdateGroupProjection> getUgAndChildUgIds(String manufacturer, String model);
}
