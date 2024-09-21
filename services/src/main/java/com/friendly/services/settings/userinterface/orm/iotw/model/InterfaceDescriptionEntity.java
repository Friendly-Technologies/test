package com.friendly.services.settings.userinterface.orm.iotw.model;

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
 * Model that represents persistence version of Interface Item
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_interface_description")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceDescriptionEntity extends AbstractEntity<Long> {

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "locale_id", nullable = false)
    private String localeId;

    @Column(name = "interface_description_id", nullable = false)
    private String interfaceDescriptionId;

}
