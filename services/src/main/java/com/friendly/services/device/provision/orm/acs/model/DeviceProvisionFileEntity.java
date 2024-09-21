package com.friendly.services.device.provision.orm.acs.model;

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
@Table(name = "cpe_file")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProvisionFileEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long cpeId;

    @Column(name = "file_type_id")
    private Long fileTypeId;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "target_file_name")
    private String targetFileName;

    @Column(name = "reprovision")
    private Integer reprovision;

    @Column(name = "filename")
    private String description;

    @Column(name = "url")
    private String url;

    //@Formula("(SELECT SUBSTRING_INDEX(c.updator, '/', -1) FROM cpe_file c WHERE c.id = id)")
    @Column(name = "updator")
    private String updater;

    /*@Formula("(SELECT SUBSTRING_INDEX(c.updator, '/', 1) FROM cpe_file c WHERE c.id = id)")
    private String application;*/

    @Formula("(SELECT c.name FROM file_type c WHERE c.id = file_type_id)")
    private String fileType;

    @Column(name = "delay_seconds")
    private Integer delay;

    @Column(name = "delivery_method")
    private Integer deliveryMethod;

    @Column(name = "delivery_protocol")
    private Integer deliveryProtocol;

}
