package com.friendly.services.device.method.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.method.orm.acs.model.CpeMethodEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpeMethodRepository extends BaseJpaRepository<CpeMethodEntity, Long>,
        PagingAndSortingRepository<CpeMethodEntity, Long> {

    @Query("SELECT n.name FROM CpeMethodNameEntity n INNER JOIN CpeMethodEntity m ON n.id = m.methodNameId " +
            "WHERE m.cpeId = :cpeId and n.name is not null")
    List<String> findMethodNamesByCpeId(Long cpeId);

    @Query("SELECT CASE WHEN count(m)>0 THEN true else false end FROM CpeMethodEntity m " +
            "inner join CpeMethodNameEntity mn ON m.methodNameId = mn.id " +
            "WHERE m.cpeId = :cpeId AND mn.name = :methodName")
    Boolean isCpeMethodExistsByCpeIdAndMethodName(Long cpeId, String methodName);

}