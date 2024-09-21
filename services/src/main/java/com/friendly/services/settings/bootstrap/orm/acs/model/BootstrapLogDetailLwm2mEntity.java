package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lwm2m_bs_session_log_details")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapLogDetailLwm2mEntity extends AbstractEntity<Integer> {

    @Column(name = "session_log_id")
    private Integer logId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "descr")
    private String description;

    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "message_origination")
    private String sender;

    @Column(name = "request_trace")
    private String request;

    @Column(name = "response_trace")
    private String response;

    @Column(name = "status")
    private String status;

}
