package com.friendly.services.settings.usergroup.orm.iotw.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Model that represents persistence version of User Role
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_user_group")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserGroupEntity extends AbstractEntity<Long> {

    @Column(name = "client_type", nullable = false, updatable = false)
    private ClientType clientType;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Column(name = "created", nullable = false, updatable = false)
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updater")
    private String updater;

    @Column(name = "template_version")
    private Integer templateVersion;

    @PrePersist
    protected void onCreate() {
        this.created = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = Instant.now();
    }
}
