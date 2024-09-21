package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.commons.models.settings.resource.ResourceType;
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
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lwm2m_coap_objects")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResourcesLwm2mEntity extends AbstractEntity<Integer> {

    @Column(name = "object_id")
    private Integer objectId;

    @Column(name = "name")
    private String name;

    @Column(name = "description1")
    private String description;

    @Column(name = "instance_type")
    private ResourceType instanceType;

    @Column(name = "lwm2m_version")
    private String version;

}
