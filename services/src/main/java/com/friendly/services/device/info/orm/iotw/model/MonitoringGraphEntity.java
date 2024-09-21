package com.friendly.services.device.info.orm.iotw.model;

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
 * Model that represents persistence version of Monitoring Graph
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_monitoring_graph")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringGraphEntity extends AbstractEntity<Long> {

    @Column(name = "monitoring_id", nullable = false, updatable = false)
    private Long monitoringId;

    @Column(name = "time", nullable = false, updatable = false)
    private Instant time;

    @Column(name = "value", length = 1024)
    private String value;

}
