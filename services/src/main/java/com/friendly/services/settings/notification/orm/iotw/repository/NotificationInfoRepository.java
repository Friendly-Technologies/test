package com.friendly.services.settings.notification.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.notification.orm.iotw.model.NotificationInfoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationInfoRepository extends BaseJpaRepository<NotificationInfoEntity, ClientType> {
}
