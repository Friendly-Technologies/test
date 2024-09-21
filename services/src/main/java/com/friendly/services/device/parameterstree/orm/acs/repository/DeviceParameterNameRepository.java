package com.friendly.services.device.parameterstree.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.ParameterNameIdTypeProjection;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link CpeParameterNameEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceParameterNameRepository extends BaseJpaRepository<CpeParameterNameEntity, Long>,
                                                       PagingAndSortingRepository<CpeParameterNameEntity, Long>,
                                                        JpaSpecificationExecutor<CpeParameterNameEntity> {

    Optional<CpeParameterNameEntity> findByName(final String name);


    Optional<CpeParameterNameEntity> findFirstByNameLike(String name);

    @Query(nativeQuery = true,
            value = "SELECT name FROM cpe_parameter_name WHERE type = 'METHOD'")
    List<String> getMethodNames();

    List<ParameterNameIdTypeProjection> findAllByNameLike(String name);
}
