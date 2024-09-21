package com.friendly.services.device.info.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseReadOnlyJpaRepository;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DeviceEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceRepository extends BaseReadOnlyJpaRepository<DeviceEntity, Long>,
                                          JpaSpecificationExecutor<DeviceEntity> {

    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.LOAD, attributePaths = {"productClass", "productClass.manufacturer", "productClass.productGroup", "customDevice"})
    List<DeviceEntity> findAll(Specification<DeviceEntity> specification);

    Optional<DeviceEntity> findFirstBySerial(final String serial);

    @Query(nativeQuery = true,
            value = "SELECT pc.group_id " +
                    "FROM cpe c " +
                    "         JOIN product_class pc ON c.product_class_id = pc.id " +
                    "WHERE c.id = ?1")

    Integer getProductGroupIdByDeviceId(int deviceId);

    boolean existsById(Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "delete from device_template_method where product_group_id = ?1")
    void deleteOldTemplateMethod(Integer groupId);


    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into device_template_method " +
                    "(product_group_id, method_name_id) " +
                    "( " +
                    "select distinct ?2, method_name_id " +
                    "from cpe_method " +
                    "where cpe_id = ?1 and method_name_id is not null " +
                    ")")
    void updateDeviceTemplateMethod(Integer deviceId, Integer groupId);

    @Query(nativeQuery = true, value = "SELECT MAX(created) FROM acs_monitoring_data")
    Timestamp getAcsMonitoringCreatedMls();


    @Query("SELECT d.domainId FROM DeviceEntity d WHERE d.id = :deviceId")
    Optional<Integer> getDomainIdById(final Long deviceId);

}
