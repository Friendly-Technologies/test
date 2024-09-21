package com.friendly.services.productclass.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * Model that represents persistence version of Product Class
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_class")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductClassEntity extends AbstractEntity<Long> {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "manuf_id")
    private Long manufId;

    @Column(name = "manufacturer_id")
    private String oui;

    @Column(name = "model")
    private String model;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manuf_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ManufacturerEntity manufacturer;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductClassGroupEntity productGroup;
}
