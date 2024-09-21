package com.friendly.services.settings.userinterface.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceSpecificDomain;
import java.util.Optional;

public interface InterfaceSpecificDomainRepository extends BaseJpaRepository<InterfaceSpecificDomain, Long> {

    Optional<InterfaceSpecificDomain> findByInterfaceItemIdAndDomainIdAndClientType(
            String interfaceId,
            Integer domainId,
            ClientType clientType);



}

