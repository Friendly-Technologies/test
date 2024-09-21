package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lwm2m_bs_object_resources")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapResourceLwm2mEntity extends AbstractEntity<Integer> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bs_id")
    private BootstrapLwm2mEntity bootstrap;

    @Column(name = "value")
    private String value;

    @Column(name = "resource_id")
    private Integer resourceId;

    @Column(name = "instance_id")
    private Integer instanceId;

    @Column(name = "resource_instance_id")
    private Integer resourceInstanceId;

    @Column(name = "object_id")
    private Integer objectId;

    @Column(name = "creator")
    private String creator;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updator")
    private String updator;

    @Column(name = "updated")
    private Instant updated;
}

