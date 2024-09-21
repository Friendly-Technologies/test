package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.ResourceDetailsLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.projections.Lwm2mResourceInstancesProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link ResourceDetailsLwm2mEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ResourceDetailsLwm2mRepository extends BaseJpaRepository<ResourceDetailsLwm2mEntity, Integer> {

    List<ResourceDetailsLwm2mEntity> findAllByObjectId(Integer objectId);

    List<ResourceDetailsLwm2mEntity> findAllByObjectIdIn(List<Integer> objectIds);

    @Query("SELECT r FROM ResourceDetailsLwm2mEntity r WHERE r.operations LIKE '%W%' and r.instanceType = 0")
    List<ResourceDetailsLwm2mEntity> findAllWritableParameters();

    @Query("SELECT r FROM ResourceDetailsLwm2mEntity r WHERE r.operations LIKE '%W%' AND r.instanceType = 1")
    List<ResourceDetailsLwm2mEntity> findAllWritableItems();

    @Query(nativeQuery = true, value = "SELECT v.instance_id as instanceId, v.resource_instance_id as resourceInstanceId, " +
            "v.value as value, r.name as name FROM lwm2m_coap_objects o, lwm2m_bs_object_resources v, lwm2m_coap_resources r " +
            "where v.bs_id = ?1" +
            "  and v.object_id = o.object_id" +
            "  and v.resource_id = r.resource_id" +
            "  and r.object_id = o.id")
    List<Lwm2mResourceInstancesProjection> findAllInstances(Integer id);


    Optional<ResourceDetailsLwm2mEntity> findByNameAndObjectId(String name, Integer objectId);

    List<ResourceDetailsLwm2mEntity> findByName(String name);

    @Query(nativeQuery = true,
            value = "SELECT cr.name\n" +
                    "FROM lwm2m_coap_resources cr INNER JOIN lwm2m_cpe_resources crp\n" +
                    "ON cr.id = crp.coap_resource_id\n" +
                    "WHERE crp.cpe_id = ?1 AND cr.operations LIKE '%W%' AND cr.instance_type = 1;")
    List<String> getLwm2mNamesForAdding(Long deviceId);
}
