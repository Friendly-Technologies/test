package com.friendly.services.device.activity.orm.acs.model;

import com.friendly.commons.models.device.TaskStateType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.Instant;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractCpeTaskEntity extends AbstractEntity<Long> {

    @Transient
    protected TaskStateType state;

    @Column(name = "cpe_id")
    private Long cpeId;

    @Column(name = "task_key")
    private Long taskKey;

    @Column(name = "created")
    private Instant created;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "type_id")
    private Integer typeId;

}
