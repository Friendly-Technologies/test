package com.friendly.services.settings.alerts.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acs_info")
public class AcsInfoEntity implements Serializable {
    @Id
    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "ipv4")
    private String ipv4;

    @Column(name = "ipv6")
    private String ipv6;

    @Column(name = "ram_total")
    private Long ramTotal;

    @Column(name = "java_threads_max")
    private Integer javaThreadsMax;

    @Column(name = "concurrent_cpes_max")
    private String concurrentCpesMax;

    @Column(name = "db_sessions_max")
    private Integer dbSessionMax;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "acs_version")
    private String acsVersion;
}
