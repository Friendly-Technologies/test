package com.friendly.services.management.profiles.orm.acs.model;

import com.friendly.services.management.events.orm.acs.model.ParameterMonitorConditionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Table(name = "profile_parameter_monitor")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProfileParameterMonitorEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "condition_id")
    private Integer conditionId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "notify_id")
    private Integer notifyId;

    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "value")
    private String value;

    @Column(name = "creator")
    private String creator;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ParameterMonitorConditionEntity parameterMonitorCondition;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "notify_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DeviceProfileParameterNotificationEntity parameterNotification;


}
