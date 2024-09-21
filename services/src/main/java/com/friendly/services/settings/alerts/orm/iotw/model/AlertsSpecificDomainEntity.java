package com.friendly.services.settings.alerts.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.AlertTimesType;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "iotw_alerts_domain_specific")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AlertsSpecificDomainEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "client_type", nullable = false)
    private ClientType clientType;
    
    @Column(name = "domain_id", nullable = false)
    private Integer domainId;

    @Column(name = "via_program")
    private boolean viaProgram;

    @Column(name = "via_email")
    private boolean viaEmail;

    @Column(name = "via_sms")
    private boolean viaSms;

    @Column(name = "via_snmp")
    private boolean viaSnmp;

    @Column(name = "alert_times_type")
    private AlertTimesType alertTimesType;

    @Column(name = "interval_time")
    private Long interval;

    @Column(name = "email")
    @ElementCollection
    @CollectionTable(name = "iotw_alert_domain_email", joinColumns = @JoinColumn(name = "alert_domain_id"))
    private Set<String> emails;

    @Column(name = "phone")
    @ElementCollection
    @CollectionTable(name = "iotw_alert_domain_phone", joinColumns = @JoinColumn(name = "alert_domain_id"))
    private Set<String> phoneNumbers;

}