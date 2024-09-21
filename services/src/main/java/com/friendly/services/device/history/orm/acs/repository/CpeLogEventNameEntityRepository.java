package com.friendly.services.device.history.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.history.orm.acs.model.CpeLogEventNameEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpeLogEventNameEntityRepository extends BaseJpaRepository<CpeLogEventNameEntity, Integer> {
    @Query(nativeQuery = true, value = "select en.name from cpe_log_event_name en where en.protocol_id = :protocolId and en.name != 'M Upload'")
    List<String> findAllByProtocolId(Integer protocolId);
}
