package com.friendly.services.productclass.orm.acs.model;

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
 * Model that represents persistence version of Manufacturer
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "manufacturer")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerEntity extends AbstractEntity<Long> {

    @Column(name = "oui")
    private String oui;

    @Column(name = "name")
    private String name;

}
