package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.commons.models.settings.bootstrap.LogStatusType;
import com.friendly.commons.models.settings.bootstrap.SecurityType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

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
@Table(name = "lwm2m_bs_session_log")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapLogLwm2mEntity extends AbstractEntity<Integer> {

    @Column(name = "bs_id")
    private Integer configId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "descr")
    private String description;

    @Column(name = "endpoint_name")
    private String endpointName;

    @Column(name = "endpoint_host")
    private String endpointHost;

    @Column(name = "endpoint_port")
    private String endpointPort;

    @Column(name = "security_type")
    @Enumerated(EnumType.STRING)
    private SecurityType securityType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LogStatusType status;

    @Formula("(SELECT c.name FROM lwm2m_bs c WHERE c.id = bs_id)")
    private String configName;

}
