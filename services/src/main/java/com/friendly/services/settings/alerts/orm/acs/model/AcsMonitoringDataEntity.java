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
@Table(name = "acs_monitoring_data")
public class AcsMonitoringDataEntity implements Serializable {
    @Id
    @Column(name = "creator")
    private String creator;
    @Column(name = "cpu_acs")
    private Integer cpuAcs;
    @Column(name = "cpu_free")
    private Integer cpuFree;
    @Column(name = "ram_acs")
    private Long ramAcs;
    @Column(name = "ram_free")
    private Long ramFree;
    @Column(name = "java_threads")
    private Integer javaThreads;
    @Column(name = "concurrent_cpes")
    private String concurrentCpes;
    @Column(name = "db_sessions")
    private Integer dbSessions;
    @Column(name = "cpe_sessions")
    private Integer cpeSessions;
    @Column(name = "failed_cpe_auths")
    private Integer failedCpeAuths;
    @Column(name = "failed_cpe_sessions")
    private Integer failedCpeSessions;
    @Column(name = "license_blocks")
    private Integer licenseBlocks;
    @Column(name = "whitelist_blocks")
    private Integer whitelistBlocks;
    @Column(name = "blacklist_blocks")
    private Integer blacklistBlocks;
    @Column(name = "created")
    private Instant created;
}
