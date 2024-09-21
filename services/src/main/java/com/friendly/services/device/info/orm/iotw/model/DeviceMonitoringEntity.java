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

/**
 * Model that represents persistence version of Monitoring
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_monitoring")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMonitoringEntity extends AbstractEntity<Long> {

    @Column(name = "session_hash", nullable = false, updatable = false)
    private String sessionHash;

    @Column(name = "cpe_id", nullable = false, updatable = false)
    private Long deviceId;

    @Column(name = "name_id", nullable = false, updatable = false)
    private Long nameId;

    @Column(name = "active")
    private boolean active;

}
