package com.friendly.services.device.activity.orm.acs.model;

import com.friendly.commons.models.device.TaskStateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.Instant;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_completed_task")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpeCompletedTaskEntity extends AbstractCpeTaskEntity {

    @Transient
    private TaskStateType state = TaskStateType.COMPLETED;

    @Column(name = "completed")
    private Instant completed;

}
