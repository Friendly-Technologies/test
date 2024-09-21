package com.friendly.services.device.diagnostics.orm.acs.model;

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
 * Model that represents persistence version of Device Diagnostics
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_diagnostic")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDiagnosticsEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long deviceId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "finished")
    private Instant completed;

    @Column(name = "name")
    private String diagnosticsType;

    @Basic(fetch=FetchType.LAZY)
    @Formula("(SELECT 'Completed' FROM cpe_completed_task c where c.cpe_id = cpe_id and c.completed is not null and c.task_key = id and c.type_id = 15 " +
            "union SELECT 'Rejected' FROM cpe_rejected_task r where r.cpe_id = cpe_id and r.task_key = id and r.type_id = 15 " +
            "union SELECT 'Failed' FROM cpe_failed_task f where f.cpe_id = cpe_id and f.task_key = id and f.type_id = 15 " +
            "union SELECT 'Pending' FROM cpe_pending_task p where p.cpe_id = cpe_id and p.repeats=0 and p.task_key = id and p.type_id = 15 " +
            "union SELECT 'Sent' FROM cpe_pending_task s where s.cpe_id = cpe_id and s.repeats>0 and s.task_key = id and s.type_id = 15) ")
    private String requestTaskState;

    @Basic(fetch= FetchType.LAZY)
    @Formula("(SELECT 'Completed' FROM cpe_completed_task c where c.cpe_id = cpe_id and c.completed is not null and c.task_key = id and c.type_id = 16 " +
            "union SELECT 'Rejected' FROM cpe_rejected_task r where r.cpe_id = cpe_id and r.task_key = id and r.type_id = 16 " +
            "union SELECT 'Failed' FROM cpe_failed_task f where f.cpe_id = cpe_id and f.task_key = id and f.type_id = 16 " +
            "union SELECT 'Pending' FROM cpe_pending_task p where p.cpe_id = cpe_id and p.repeats=0 and p.task_key = id and p.type_id = 16 " +
            "union SELECT 'Sent' FROM cpe_pending_task s where s.cpe_id = cpe_id and s.repeats>0 and s.task_key = id and s.type_id = 16) ")
    private String completeTaskState;

    @Formula("(SELECT d.value FROM cpe_get_diagnostic d " +
            "inner join cpe_parameter_name n on d.name_id = n.id " +
            "where d.cpe_diagnostic_id = id and (n.name like '%.DiagnosticsState' or n.name like '%.Status') LIMIT 1 ) ")
    private String state;

}
