package com.friendly.services.filemanagement.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

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
@Table(name = "cpe_file")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceFileDownloadEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long deviceId;

    @Column(name = "created")
    private Instant created;

    //@Formula("(SELECT SUBSTRING_INDEX(c.creator, '/', -1) FROM cpe_file c WHERE c.id = id)")
    @Column(name = "creator")
    private String creator;

/*    @Formula("(SELECT SUBSTRING_INDEX(c.creator, '/', 1) FROM cpe_file c WHERE c.id = id)")
    private String application;*/

    @Column(name = "filename")
    private String fileName;

    @Column(name = "url")
    private String url;

    @Column(name = "target_file_name")
    private String targetFileName;

    @Formula("(SELECT e.name FROM file_type e WHERE e.id = file_type_id)")
    private String fileType;

    @Column(name = "file_type_id")
    private Long fileTypeId;

    @Formula("(SELECT 'Completed' FROM cpe_completed_task c where c.cpe_id = cpe_id and c.completed is not null and c.task_key = id and c.type_id = 29 " +
            "union SELECT 'Rejected' FROM cpe_rejected_task r where r.cpe_id = cpe_id and r.task_key = id and r.type_id = 29 " +
            "union SELECT 'Failed' FROM cpe_failed_task f where f.cpe_id = cpe_id and f.task_key = id and f.type_id = 29 " +
            "union SELECT 'Pending' FROM cpe_pending_task p where p.cpe_id = cpe_id and p.repeats=0 and p.task_key = id and p.type_id = 29 " +
            "union SELECT 'Sent' FROM cpe_pending_task s where s.cpe_id = cpe_id and s.repeats>0 and s.task_key = id and s.type_id = 29 LIMIT 1)  ")
    private String state;

    @Formula("(SELECT c.completed FROM cpe_completed_task c where c.cpe_id = cpe_id and c.completed is not null and c.task_key = id and c.type_id = 29 ORDER BY c.created DESC LIMIT 1) ")
    private Instant completed;

}
