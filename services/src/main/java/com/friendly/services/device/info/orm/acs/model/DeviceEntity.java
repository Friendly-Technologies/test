package com.friendly.services.device.info.orm.acs.model;

import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode
@Entity
@Table(name = "cpe")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "created")
    private Instant created;

    @Formula("(select case when d.name is null then 'Super domain' else d.name end from cpe c " +
            "left join isp d on c.location_id = d.id where c.id = id)")
    private String domainName;

    @Column(name = "location_id")
    private Integer domainId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_class_id", referencedColumnName = "id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private ProductClassEntity productClass;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "serial", referencedColumnName = "serial", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private CustomDeviceEntity customDevice;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpe_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<CpeParameterEntity> cpeParameters;

    @Column(name = "is_online")
    private Integer isOnline;

    @Column(name = "active_conn_name_id")
    private Integer activeConnectionNameId;

    @Column(name = "serial")
    private String serial;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "firmware")
    private String firmware;

    @Column(name = "protocol_id")
    private Integer protocolId;

    @Formula("(select count(t.id) from cpe_completed_task t where t.cpe_id = id)")
    private Integer completedTasks;

    @Formula("(select count(t.id) from cpe_failed_task t where t.cpe_id = id)")
    private Integer failedTasks;

    @Formula("(select count(t.id) from cpe_pending_task t where t.cpe_id = id)")
    private Integer pendingTasks;

    @Formula("(select count(t.id) from cpe_rejected_task t where t.cpe_id = id)")
    private Integer rejectedTasks;

    @Formula("(select sum(case when t.repeats = 0 then 0 else 1 end) from cpe_pending_task t where t.cpe_id = id)")
    private Integer sentTasks;

}
