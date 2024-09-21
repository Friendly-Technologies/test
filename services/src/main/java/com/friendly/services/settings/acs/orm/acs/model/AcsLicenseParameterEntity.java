package com.friendly.services.settings.acs.orm.acs.model;

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
 * Model that represents persistence version of ACS License Parameter
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ftacs_parameter")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsLicenseParameterEntity extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @Column(name = "protocol_id")
    private Integer protocolId;

}
