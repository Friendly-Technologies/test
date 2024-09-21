package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeCustomParameterEntity;
import com.friendly.services.management.profiles.orm.acs.model.GroupConditionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link CpeCustomParameterEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface GroupConditionRepository extends BaseJpaRepository<GroupConditionEntity, Long> {
    @Query("SELECT p FROM GroupConditionEntity p WHERE p.productClassGroup.manufacturerName = :manufacturer " +
            "and p.productClassGroup.model = :model")
    List<GroupConditionEntity> findAllByManufacturerAndModel(String manufacturer, String model);
    @Query("SELECT p FROM GroupConditionEntity p WHERE p.productClassGroup.manufacturerName = :manufacturer " +
            "and p.productClassGroup.model = :model and p.domainId in :domainIds")
    List<GroupConditionEntity> findAllByManufacturerAndModel(String manufacturer, String model, List<Integer> domainIds);

    @Query("SELECT p FROM GroupConditionEntity p WHERE p.name = :name and p.productClassGroup.manufacturerName = :manufacturer " +
            "and p.productClassGroup.model = :model")
    Optional<Long> getByNameManufacturerAndModel(String name, String manufacturer, String model);
}
