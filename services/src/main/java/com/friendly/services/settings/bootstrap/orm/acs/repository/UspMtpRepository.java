package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityUspMtpEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UspMtpRepository extends BaseJpaRepository<SecurityUspMtpEntity, Integer> {

   /* void deleteAllBySecurity(SecurityUspEntity id);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM iot_sec_conf_details WHERE security_configuration_id = ?1")
    void deleteAllBySecurityId(Integer id);*/

    @Modifying
    @Transactional
    @Query("DELETE FROM SecurityUspMtpEntity s WHERE s.security.id = :securityConfigurationId")
    void deleteAllBySecurityId(@Param("securityConfigurationId") Integer securityConfigurationId);


}
