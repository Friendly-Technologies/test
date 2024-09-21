package com.friendly.services.device.provision.orm.acs.model;

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
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "custom_rpc")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRpcEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long cpeId;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "request_message")
    private String request;

    @Column(name = "reprovision")
    private Integer reprovision;

    @Column(name = "updator")
    private String updater;

    /*@Formula("(SELECT SUBSTRING_INDEX(c.updator, '/', -1) FROM custom_rpc c WHERE c.id = id)")
    private String updater;*/

    /*@Formula("(SELECT SUBSTRING_INDEX(c.updator, '/', 1) FROM custom_rpc c WHERE c.id = id)")
    private String application;*/

}
