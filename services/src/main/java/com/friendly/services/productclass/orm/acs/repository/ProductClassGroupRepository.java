package com.friendly.services.productclass.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseReadOnlyJpaRepository;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository to interact with persistence layer to store {@link ProductClassGroupEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ProductClassGroupRepository extends BaseReadOnlyJpaRepository<ProductClassGroupEntity, Long> {

    @Query("SELECT m FROM ProductClassGroupEntity m " +
            "WHERE (:protocolId is null or m.protocolId = :protocolId)")
    List<ProductClassGroupEntity> getProductClassesByProtocolId(final Integer protocolId);

    @Query("SELECT m FROM ProductClassGroupEntity m " +
            "WHERE  m.protocolId = 0")
    List<ProductClassGroupEntity> getProductClassesByTR069Protocol();

    @Query("SELECT DISTINCT m.oui FROM ProductClassEntity m " +
            "WHERE m.groupId = :productClassId ORDER BY m.oui")
    List<String> getOUIsByProductClassId(final Long productClassId);

    @Query("SELECT distinct pc.groupId " +
            "FROM ProductClassEntity pc, CpeEntity c " +
            "WHERE c.productClassId=pc.id and pc.groupId in :ids")
    List<Long> getUsedId(List<Long> ids);

    @Query("select m from ProductClassGroupEntity m where m.id in :ids")
    Page<ProductClassGroupEntity> findAll(final List<Long> ids, final Pageable pageable);

    List<ProductClassGroupEntity> findAllByIdIn (Set<Long> ids);

    @Query("select m.id from ProductClassGroupEntity m")
    List<Long> findAllIds();

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductClassGroupEntity p WHERE p.id in :ids")
    int deleteUnusedModels(List<Long> ids);

    ProductClassGroupEntity findByManufacturerNameAndModelAndProtocolId(String manufacturer, String model, Integer protocolId);

    Optional<ProductClassGroupEntity> findByManufacturerNameAndModel(String manufacturer, String model);

    Optional<Long> findIdByManufacturerNameAndModel(String manufacturer, String model);

    Optional<ProductClassGroupEntity> findFirstByManufacturerNameAndModelOrderById(String manufacturer, String model);

    List<ProductClassGroupEntity> findAllByManufacturerNameAndModel(String manufacturer, String model);

    @Query("SELECT p.groupId from ProductClassEntity p WHERE p.id = :id")
    Long getGroupIdByProductClassId(Long id);

    @Query(nativeQuery = true, value = "select m.protocol_id from product_class_group m where m.manufacturer_name = :manufacturer and m.product_class = :model limit 1")
    Integer getProtocolIdByManufacturerAndModel(String manufacturer, String model);

    @Query("select m.id from ProductClassGroupEntity m where m.manufacturerName = :manufacturer and m.model = :model")
    Long getIdByManufacturerAndModel(String manufacturer, String model);

    @Query("SELECT CASE WHEN count(p)>0 THEN true else false end FROM CpeParameterEntity p " +
            "inner join CpeParameterNameEntity pn ON p.nameId = pn.id " +
            "inner join CpeEntity c ON p.cpeId = c.id " +
            "inner join ProductClassEntity pc ON c.productClassId = pc.id " +
            "WHERE pc.productGroup.manufacturerName = :manufacturer AND pc.productGroup.model = :model  AND pn.name like :param")
    Boolean isParamExistLikeForManufacturerAndModel(String manufacturer, String model, final String param);
}
