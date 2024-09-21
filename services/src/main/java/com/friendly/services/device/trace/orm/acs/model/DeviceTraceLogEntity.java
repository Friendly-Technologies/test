package com.friendly.services.device.trace.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Model that represents persistence version of CPE Trace Log
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_trace_log")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTraceLogEntity extends AbstractEntity<Integer> {

    @Column(name = "cpe_id")
    private Long deviceId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "value")
    private String value;

    @Column(name = "previous_id")
    private Integer previousId;

}
