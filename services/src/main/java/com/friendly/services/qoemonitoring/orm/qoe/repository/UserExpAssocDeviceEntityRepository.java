package com.friendly.services.qoemonitoring.orm.qoe.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.UserExpAssocDeviceEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.UserExpAssocDeviceEntityId;
import com.friendly.services.qoemonitoring.orm.qoe.model.projections.UserExpAssocDeviceProjection;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface UserExpAssocDeviceEntityRepository extends BaseJpaRepository<UserExpAssocDeviceEntity, UserExpAssocDeviceEntityId> {
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT c.serial as serial, c.name_id as nameId," +
                    " c.created as created, c.mac as mac, c.rssi as rssi," +
                    "c.signal as signal "
                    + "FROM ftacs_qoe_ui_data_acsrd.user_exp_assoc_device c "
                    + "WHERE c.serial = ?1 "
                    + "AND c.created >= ?2 "
                    + "order by 1")
    List<UserExpAssocDeviceProjection> findAllBySerialAndCreated(String serial, Instant dateFrom);
}
