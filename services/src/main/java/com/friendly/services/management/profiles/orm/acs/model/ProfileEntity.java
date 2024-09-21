package com.friendly.services.management.profiles.orm.acs.model;

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
@Table(name = "profile")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "version")
    private String version;

    @Column(name = "location_id")
    private Integer domainId;


}
