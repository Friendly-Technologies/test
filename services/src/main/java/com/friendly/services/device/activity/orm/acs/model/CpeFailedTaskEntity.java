package com.friendly.services.device.activity.orm.acs.model;

import com.friendly.commons.models.device.TaskStateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_failed_task")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpeFailedTaskEntity extends AbstractCpeTaskEntity {

    @Transient
    private TaskStateType state = TaskStateType.FAILED;

    @Formula("(select distinct l.fault_code FROM error_log l where l.task_id = id)")
    private Integer faultCode;

    @Formula("(select distinct en.description FROM error_log l left join error_log_errortext_name en on l.error_text_id = en.id " +
            "where l.task_id = id)")
    private String description;
}
