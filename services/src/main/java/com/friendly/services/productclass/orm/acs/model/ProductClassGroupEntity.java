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
@Table(name = "product_class_group")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductClassGroupEntity extends AbstractEntity<Long> {

    @Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column(name = "product_class")
    private String model;

    @Column(name = "protocol_id")
    private Integer protocolId;

}
