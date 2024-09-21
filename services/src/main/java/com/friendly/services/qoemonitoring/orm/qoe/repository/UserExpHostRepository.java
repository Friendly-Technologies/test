package com.friendly.services.qoemonitoring.orm.qoe.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.qoemonitoring.orm.qoe.model.UserExpHostEntity;
import com.friendly.services.qoemonitoring.orm.qoe.model.UserExpHostEntityId;
import com.friendly.services.qoemonitoring.orm.qoe.model.projections.UserExpHostProjection;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface UserExpHostRepository extends BaseJpaRepository<UserExpHostEntity, UserExpHostEntityId> {
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT c.serial as serial, c.name_id as nameId," +
                    " c.created as created, c.name as hostName, " +
                    "c.mac as mac, interface_type as interfaceType, " +
                    "c.layer1 as layer1, c.layer3 as layer3, c.active as active  "
                    + "FROM ftacs_qoe_ui_data_acsrd.user_exp_host c "
                    + "WHERE c.serial = ?1 "
                    + "AND c.created >= ?2 "
                    + "order by 1")
    List<UserExpHostProjection> findAllBySerialAndCreated(String serial, Instant dateFrom);
}
