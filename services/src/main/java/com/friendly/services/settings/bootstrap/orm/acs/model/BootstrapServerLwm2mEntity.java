package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lwm2m_bs_server")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapServerLwm2mEntity extends AbstractEntity<Integer> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bs_id")
    private BootstrapLwm2mEntity bootstrap;

    @Column(name = "instance_id")
    private Integer instanceId;

    @Column(name = "short_server_id")
    private String serverId;

    @Column(name = "server_uri")
    private String serverUri;

    @Column(name = "lifeTime")
    private Integer lifeTime;

    @Column(name = "def_min_period")
    private Integer minPeriod;

    @Column(name = "def_max_period")
    private Integer maxPeriod;

    @Column(name = "disable_timeout")
    private Integer disableTimeout;

    @Column(name = "notification_storing")
    private Boolean notification;

    @Column(name = "binding")
    private String binding;

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updater;

}
