package com.friendly.services.device.info.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Model that represents persistence version of Domain
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "isp")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEntity extends AbstractEntity<Integer> {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Transient
    private String parentId;

    @Transient
    private String fullName;

    @Transient
    private List<DomainEntity> domains;

}
