package com.friendly.services.settings.acs.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.acs.orm.acs.model.AcsLicenseParameterEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Repository to interact with persistence layer to store {@link AcsLicenseParameterEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface LicenceRepository extends BaseJpaRepository<AcsLicenseParameterEntity, Integer>,
                                           PagingAndSortingRepository<AcsLicenseParameterEntity, Integer> {

    @Query(nativeQuery = true, value = "SELECT value, created FROM license l ORDER BY created DESC ")
    List<Map<String, Object>> getAcsLicenses();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "INSERT INTO license (value, created) VALUES (?1, ?2)")
    void addAcsLicense(final String value, final Instant created);

    @Query(nativeQuery = true, value = "SELECT 1")
    void isHealth();
}
