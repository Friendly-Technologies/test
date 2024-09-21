package com.friendly.services.qoemonitoring.orm.qoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wifi_collisions")
@Entity
@Data
@IdClass(WifiCollisionEntityId.class)
public class WifiCollisionEntity implements Serializable {
    @Id
    private Instant created;

    @Column(name = "ssid")
    private String name;

    @Column(name = "channel")
    private Integer channel;

    @Column(name = "signal")
    private Integer value;

    @Column(name = "name_id")
    private Integer nameId;

    @Column(name = "serial")
    private String serial;

}
