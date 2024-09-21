package com.friendly.services.device.info.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseReadOnlyJpaRepository;
import com.friendly.services.device.info.orm.acs.model.DomainEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link DomainEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DomainRepository extends BaseReadOnlyJpaRepository<DomainEntity, Integer> {

    @Query("SELECT d FROM DomainEntity d WHERE :id is null OR d.id = :id OR d.name like " +
            "concat((SELECT d.name FROM DomainEntity d WHERE d.id = :id),'.%')")
    List<DomainEntity> getDomains(final Integer id);

    @Query("SELECT d.id FROM DomainEntity d WHERE d.name = :name OR d.name like " +
            "concat((SELECT d.name FROM DomainEntity d WHERE d.name = :name),'.%')")
    List<Integer> getDomainIdsByName(final String name);

    @Query("SELECT d.id FROM DomainEntity d WHERE d.name = :name")
    Optional<Integer> getDomainIdByName(final String name);

    @Query("SELECT d.name FROM DomainEntity d WHERE d.id = :id")
    Optional<String> getDomainNameById(final Integer id);
}
