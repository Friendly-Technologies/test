package com.friendly.services.device.parameterstree.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeCustomParameterEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link CpeCustomParameterEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceCustomParameterRepository extends BaseJpaRepository<CpeCustomParameterEntity, Long> {

    List<CpeCustomParameterEntity> findAllByCpeId(final Long cpeId);

    @Query("SELECT p FROM CpeCustomParameterEntity p INNER JOIN CpeParameterNameEntity pn ON p.nameId = pn.id " +
            "WHERE p.cpeId = :cpeId and pn.name like :fullName")
    List<CpeCustomParameterEntity> findAllByCpeIdAndFullName(final Long cpeId, final String fullName);

}
