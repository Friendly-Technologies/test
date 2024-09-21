package com.friendly.services.device.method.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.method.orm.acs.model.CpeMethodNameEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CpeMethodNameRepository extends BaseJpaRepository<CpeMethodNameEntity, Long>,
        PagingAndSortingRepository<CpeMethodNameEntity, Long> {
    @Query("SELECT c.id FROM CpeMethodNameEntity c WHERE lower(c.name) = lower(:methodName)")
    Integer getIdByMethodName(String methodName);

    CpeMethodNameEntity getCpeMethodNameEntityById(Long id);
}