package com.friendly.services.settings.alerts.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.alerts.orm.iotw.model.AlertsSpecificDomainEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertsSpecificDomainRepository extends BaseJpaRepository<AlertsSpecificDomainEntity, Integer> {
    Optional<AlertsSpecificDomainEntity> findByClientTypeAndDomainId(ClientType clientType, Integer domainId);
}
