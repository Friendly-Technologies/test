package com.friendly.services.settings.usergroup.orm.iotw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model that represents persistence version of Permission State
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Entity
@Table(name = "iotw_permission_state")
@Data
@IdClass(PermissionPK.class)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PermissionStateEntity implements Serializable {

    @Id
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Id
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Id
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionStateType type;

    @Column(name = "checked")
    private boolean checked;

    @Column(name = "visible")
    private boolean visible;

}
