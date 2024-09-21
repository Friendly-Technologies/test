package com.friendly.services.device.activity.orm.acs.model;

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
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "custom_rpc_history")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRpcHistoryEntity extends AbstractEntity<Long> {

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "method_name")
    private String method;

    @Column(name = "request_message", length = 4000)
    private String request;

    @Column(name = "response_message", length = 4000)
    private String response;

    //@Formula("(SELECT SUBSTRING_INDEX(c.creator, '/', -1) FROM custom_rpc_history c WHERE c.id = id)")
    @Column(name = "creator")
    private String creator;

    /*@Formula("(SELECT SUBSTRING_INDEX(c.creator, '/', 1) FROM custom_rpc_history c WHERE c.id = id)")
    private String application;*/

}
