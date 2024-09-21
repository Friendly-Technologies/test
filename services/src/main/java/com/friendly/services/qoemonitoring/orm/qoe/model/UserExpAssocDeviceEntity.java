package com.friendly.services.qoemonitoring.orm.qoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_exp_assoc_device")
@Entity
@Data
public class UserExpAssocDeviceEntity implements Serializable {
    @Id
    private Instant created;

    @Column(name = "serial")
    private String serial;

    @Column(name = "name_id")
    private Integer nameId;

    @Column(name = "mac")
    private String mac;

    @Column(name = "rssi")
    private String rssi;

    @Column(name = "signal")
    private String singal;
}
