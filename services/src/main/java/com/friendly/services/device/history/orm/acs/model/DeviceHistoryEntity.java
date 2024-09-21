package com.friendly.services.device.history.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_log")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceHistoryEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long deviceId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "event_code_id")
    private Integer eventCodeId;

    @Formula("(SELECT e.name FROM cpe_log_event_name e WHERE e.id = event_code_id)")
    private String activityType;

}
