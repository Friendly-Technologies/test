package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.Instant;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lwm2m_bs_security")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapSecurityLwm2mEntity extends AbstractEntity<Integer> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bs_id")
    private BootstrapLwm2mEntity bootstrap;

    @Column(name = "security_id")
    private Integer securityId;

    @Column(name = "instance_id")
    private Integer instanceId;

    @Column(name = "short_server_id")
    private String serverId;

    @Column(name = "os_instance_id")
    private Integer osInstanceId;

    @Column(name = "hold_off_time")
    private Integer holdOffTime;

    @Column(name = "server_uri")
    private String serverUri;

    @Column(name = "is_bootstrap")
    private Boolean isBootstrap;

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updater;

    @Formula("(SELECT s.security_mode FROM lwm2m_security s WHERE s.id = security_id)")
    private SecurityModeType securityType;


}
