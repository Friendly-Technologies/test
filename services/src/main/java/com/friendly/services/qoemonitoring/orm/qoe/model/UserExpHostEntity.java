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
@Table(name = "user_exp_host")
@Entity
@Data
public class UserExpHostEntity implements Serializable {
    @Id
    private Instant created;

    @Column(name = "serial")
    private String serial;

    @Column(name = "name_id")
    private Integer nameId;

    @Column(name = "name")
    private String hostName;

    @Column(name = "mac")
    private String mac;

    @Column(name = "interface_type")
    private String interfaceType;

    @Column(name = "layer1")
    private String layer1;

    @Column(name = "layer3")
    private String layer3;

    @Column(name = "addr")
    private String addr;

    @Column(name = "active")
    private String active;
}
