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
@Table(name = "cpe_provision_object")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProvisionObjectEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long cpeId;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "name_id")
    private Long nameId;

    //@Formula("(SELECT SUBSTRING_INDEX(c.creator, '/', -1) FROM cpe_provision_object c WHERE c.id = id)")
    @Column(name = "creator")
    private String updater;

    @Column(name = "reprovision")
    private Integer reprovision;

    /*@Formula("(SELECT SUBSTRING_INDEX(c.creator, '/', 1) FROM cpe_provision_object c WHERE c.id = id)")
    private String application;*/

}
