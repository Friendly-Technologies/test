package com.friendly.services.uiservices.view.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.view.ViewType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Model that represents persistence version of Column Name
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_view")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ViewEntity extends AbstractEntity<Long> {

    @Column(name = "client_type")
    private ClientType clientType;

    @Column(name = "domain_id")
    private Integer domainId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "view_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ViewType type;

    @Column(name = "default_user")
    private Boolean defaultUser;

    @Column(name = "default_domain")
    private Boolean defaultDomain;

    @Column(name = "width")
    private Integer tableWidth;
}
