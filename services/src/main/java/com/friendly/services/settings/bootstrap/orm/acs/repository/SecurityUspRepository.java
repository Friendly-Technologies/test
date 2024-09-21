package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityUspEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link SecurityUspEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface SecurityUspRepository extends BaseJpaRepository<SecurityUspEntity, Integer> {

    Page<SecurityUspEntity> findAllByDomainIdIn(List<Integer> domainIds, Pageable pageable);

    Page<SecurityUspEntity> findAllByDomainIdInAndIdentifier(List<Integer> domainIds, String identifier,
                                                             Pageable pageable);

    Page<SecurityUspEntity> findAllByDomainIdInAndIdentifierLike(List<Integer> domainIds, String identifier,
                                                                 Pageable pageable);

    @Query("select case when count(s)> 0 then true else false end from SecurityMqttEntity s " +
            "where (:id is null or s.id <> :id) and s.mask like :mask")
    boolean existsByIdNotAndMaskLike(Integer id, String mask);

    boolean existsByIdentifier(String name);
}
