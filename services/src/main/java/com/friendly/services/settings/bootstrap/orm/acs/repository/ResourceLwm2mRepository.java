package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.ResourcesLwm2mEntity;
import java.util.List;

import com.friendly.services.settings.bootstrap.orm.acs.model.projections.Lwm2mResourceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository to interact with persistence layer to store {@link ResourcesLwm2mEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ResourceLwm2mRepository extends BaseJpaRepository<ResourcesLwm2mEntity, Integer>,
        JpaSpecificationExecutor<ResourcesLwm2mEntity> {
    @Query("SELECT o " +
            "FROM ResourcesLwm2mEntity o " +
            "where lower(o.name) != 'lwm2m server' " +
            "  and exists ( " +
            "        select 1 from ResourceDetailsLwm2mEntity r" +
            "        where r.objectId = o.id " +
            "          and r.operations like '%W%')")
    Page<ResourcesLwm2mEntity> getAll(Pageable pageable);

    @Query("SELECT o " +
            "FROM ResourcesLwm2mEntity o " +
            "where lower(o.name) != 'lwm2m server' and o.name like :searchParam " +
            "  and exists ( " +
            "        select 1 from ResourceDetailsLwm2mEntity r" +
            "        where r.objectId = o.id " +
            "          and r.operations like '%W%')")
    Page<ResourcesLwm2mEntity> getAllByParam(Pageable pageable, String searchParam);


    @Query(nativeQuery = true,
            value = "SELECT o.id as id, o.object_id as objectId, o.name as name, o.description1 as  description, o.instance_type as instanceType, o.lwm2m_version as version " +
            "            FROM lwm2m_coap_objects o " +
            "            where lower(o.name) != 'lwm2m server' " +
            "              and exists ( " +
            "                    select 1 from  ftacs.lwm2m_coap_resources r " +
            "                   where r.object_id = o.id " +
            "                     and r.operations like '%W%') " +
            "            and o.name in (SELECT DISTINCT s.name " +
            "                           FROM  ftacs.lwm2m_coap_objects s,  ftacs.lwm2m_bs_object_resources v,  ftacs.lwm2m_coap_resources r " +
            "                           where v.bs_id = ?1 " +
            "                             and v.object_id = s.object_id " +
            "                             and v.resource_id = r.resource_id " +
            "                             and r.object_id = s.id " +
            "                           order by r.name, v.instance_id, v.resource_instance_id);")
    List<Lwm2mResourceProjection> getAll(Integer id);

    /*@Query("SELECT l " +
            "FROM ResourcesLwm2mEntity l " +
            "where l.name = :name " +
            "and exists (" +
            "select 1 from ResourceDetailsLwm2mEntity d " +
            "where d.objectId = l.id  " +
            ")")
    ResourcesLwm2mEntity getObjectIdForNewParams1(String name);*/

    @Query(nativeQuery = true,
            value = "SELECT id " +
                    "FROM lwm2m_coap_objects " +
                    "where name= ?1 " +
                    "and exists ( " +
                    "select 1 from lwm2m_coap_resources r " +
                    "where r.object_id = lwm2m_coap_objects.id  " +
                    ")")
    Integer getObjectIdForNewParams(String name);


    @Query(nativeQuery = true,
            value = "SELECT name " +
                    "FROM lwm2m_coap_resources " +
                    "where operations like '%W%' and object_id = ?1 " +
                    "order by name")
    List<String> getShortNamesForNewParams(int objectId);

    @Query(nativeQuery = true,
          value = "SELECT instance_type " +
                  "FROM lwm2m_coap_resources " +
                  "where name = ?1 and object_id = ?2")
    Integer getInstanceTypeForParamName(String name, int objectId);

    @Query(nativeQuery = true,
            value = "SELECT distinct path\n" +
                    "FROM lwm2m_cpe_resources\n" +
                    "where cpe_id=?1 and path like '/9/_'\n" +
                    "order by 1")
    List<String> getTargetFileNamesForDevice(Integer deviceId);
}
