package com.friendly.services.uiservices.statistic.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.DeviceActivityType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.uiservices.statistic.DeviceActivityTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Model that represents persistence version of Device Activity Log
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_device_activity_log")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLogEntity extends AbstractEntity<Long> {

    @Column(name = "client_type", nullable = false, updatable = false)
    private ClientType clientType;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "created")
    private Instant date;

    @Column(name = "act_type")
    @Convert(converter = DeviceActivityTypeConverter.class)
    private DeviceActivityType activityType;

    @Column(name = "device_id", nullable = false, updatable = false)
    private Long deviceId;

    @Column(name = "serial")
    private String serial;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "note")
    private String note;

}
