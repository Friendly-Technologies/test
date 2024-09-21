package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.commons.models.settings.security.ServerType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityLwm2mEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link SecurityLwm2mEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface SecurityLwm2mRepository extends BaseJpaRepository<SecurityLwm2mEntity, Integer> {

    Page<SecurityLwm2mEntity> findAllByDomainIdInAndMask(List<Integer> domainIds, String identifier, Pageable pageable);

    Page<SecurityLwm2mEntity> findAllByDomainIdInAndMaskLike(List<Integer> domainIds,
                                                                          String identifier,
                                                                          Pageable pageable);

    Page<SecurityLwm2mEntity> findAllByDomainIdIn(List<Integer> domainIds, Pageable pageable);

    Page<SecurityLwm2mEntity> findAllByDomainIdInAndServerType(List<Integer> domainIds, ServerType serverType,
                                                               Pageable pageable);

    Page<SecurityLwm2mEntity> findAllByDomainIdInAndMaskAndServerType(List<Integer> domainIds, String identifier,
                                                                      ServerType serverType, Pageable pageable);

    Page<SecurityLwm2mEntity> findAllByDomainIdInAndMaskLikeAndServerType(List<Integer> domainIds,
                                                                          String identifier,
                                                                          ServerType serverType,
                                                                          Pageable pageable);

    @Query("select case when count(s)> 0 then true else false end from SecurityLwm2mEntity s " +
            "where (:id is null or s.id <> :id) and s.senderId = :senderId")
    boolean existsByIdNotAndSenderId(Integer id, String senderId);

    @Query("select case when count(s)> 0 then true else false end from SecurityLwm2mEntity s " +
            "where (:id is null or s.id <> :id) and s.identity = :identity")
    boolean existsByIdNotAndIdentity(Integer id, String identity);

    @Query("select case when count(s)> 0 then true else false end from SecurityLwm2mEntity s " +
            "where (:id is null or s.id <> :id) and s.securityMode = :securityMode and s.mask like :mask")
    boolean existsByIdNotAndSecurityModeAndMaskLike(Integer id, Integer securityMode, String mask);

    @Query(nativeQuery = true,
            value = "SELECT r.name " +
            "FROM lwm2m_coap_objects o, lwm2m_bs_object_resources v, lwm2m_coap_resources r " +
            "WHERE v.bs_id = ?1 " +
            " AND v.object_id = o.object_id " +
            " AND v.resource_id = ?2 " +
            " AND v.resource_id = r.resource_id " +
            " AND r.object_id = o.id")
    String getResourceName(Integer bsId, Integer resourceId);

    @Query(nativeQuery = true,
            value = "SELECT r.instance_type " +
                    "FROM lwm2m_coap_objects o, lwm2m_bs_object_resources v, lwm2m_coap_resources r " +
                    "WHERE v.bs_id = ?1 " +
                    " AND v.object_id = o.object_id " +
                    " AND v.resource_id = ?2 " +
                    " AND v.resource_id = r.resource_id " +
                    " AND r.object_id = o.id")
    Integer getInstanceType(Integer bsId, Integer resourceId);

    @Query(nativeQuery = true,
            value = "SELECT o.name FROM lwm2m_coap_objects o WHERE o.object_id = ?1")
    String getResourceObjectNameById(Integer id);
}
