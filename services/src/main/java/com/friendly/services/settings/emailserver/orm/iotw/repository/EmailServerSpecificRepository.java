package com.friendly.services.settings.emailserver.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.emailserver.orm.iotw.model.EmailServerSpecificEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailServerSpecificRepository extends BaseJpaRepository<EmailServerSpecificEntity, Integer> {
    Optional<EmailServerSpecificEntity> findByDomainIdAndClientType(Integer domainId, ClientType clientType);
}
