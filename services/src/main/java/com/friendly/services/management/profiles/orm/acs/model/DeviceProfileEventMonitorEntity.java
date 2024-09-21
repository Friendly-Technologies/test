package com.friendly.services.management.profiles.orm.acs.model;

import com.friendly.services.device.history.orm.acs.model.CpeLogEventNameEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Table(name = "profile_event_monitor")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProfileEventMonitorEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created")
    private Instant created;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "event_name_id")
    private Integer eventNameId;

    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "status")
    private Integer status;

    @Column(name = "creator")
    private String creator;


    @ManyToOne
    @JoinColumn(name = "event_name_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CpeLogEventNameEntity cpeLogEventNameEntity;
}
