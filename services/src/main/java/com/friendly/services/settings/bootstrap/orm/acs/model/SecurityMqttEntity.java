package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.commons.models.settings.security.MaskType;
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
import java.time.Instant;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mqtt_security")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SecurityMqttEntity extends AbstractEntity<Integer> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updater;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "client_mask")
    private String mask;

    @Column(name = "client_mask_type")
    @Enumerated(EnumType.STRING)
    private MaskType maskType;

    @Column(name = "client_login")
    private String login;

    @Column(name = "client_psw")
    private String password;

    @Column(name = "security_mode")
    private Integer securityType;

    @Column(name = "comment_data")
    private String commentData;

}
