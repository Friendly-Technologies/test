package com.friendly.services.qoemonitoring.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "qoe_monitoring")
@Data
@SuperBuilder
@NoArgsConstructor
public class QoeMonitoringEntity extends AbstractEntity<Integer> {

    @Column(name = "group_id")
    private Long groupId;
}
