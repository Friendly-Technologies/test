package com.friendly.services.device.parameterstree.orm.acs.model;

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
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_parameter_name")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpeParameterNameEntity extends AbstractEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "encrypted")
    private Boolean encrypted;
}
