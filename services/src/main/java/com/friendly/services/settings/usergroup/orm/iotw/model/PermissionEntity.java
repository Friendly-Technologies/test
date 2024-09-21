package com.friendly.services.settings.usergroup.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
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

/**
 * Model that represents persistence version of User Role Permission
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_permission")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity extends AbstractEntity<Long> {

    @Column(name = "client_type", nullable = false, updatable = false)
    private ClientType clientType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "icon_path")
    private String iconPath;

    @Column(name = "type")
    private String type;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "index_visible")
    private Integer index;

    @Column(name = "location")
    private String location;
    @Transient
    private PermissionStateEntity viewState;

    @Transient
    private PermissionStateEntity executeState;

}
