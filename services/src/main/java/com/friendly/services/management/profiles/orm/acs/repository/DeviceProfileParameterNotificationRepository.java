package com.friendly.services.management.profiles.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.management.profiles.orm.acs.model.DeviceProfileParameterNotificationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceProfileParameterNotificationRepository extends BaseJpaRepository<DeviceProfileParameterNotificationEntity, Integer> {
    List<DeviceProfileParameterNotificationEntity> findAllByProfileId(Integer id);

    @Query("SELECT n.name FROM DeviceProfileParameterNotificationEntity n INNER JOIN DeviceProfileParameterMonitorEntity m ON m.id = :id WHERE m.notifyId = n.id")
    String findNotificationNameByMonitorId(final Integer id);
}
