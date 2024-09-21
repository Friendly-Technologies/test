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
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iot_configuration_property")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IotConfigPropertyEntity extends AbstractEntity<Long> {

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "value")
    private String value;

    @Column(name = "value_type")
    private String valueType;

    @Column(name = "min_value")
    private Integer minValue;

    @Column(name = "max_value")
    private Integer maxValue;

    @Column(name = "valid_values")
    private String validValues;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "overridable")
    private Boolean overridable;

    @Column(name = "writable")
    private Boolean writable;

}
