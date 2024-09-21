package com.friendly.services.settings.alerts.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.AlertTimesType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

/**
 * Model that represents persistence version of Database Setting
 *
 * @author Friendly Tech
 * @since 0.0.2
 */

@Entity
@Table(name = "iotw_alerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertsEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private ClientType id;

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
    @CollectionTable(name = "iotw_alert_email", joinColumns = @JoinColumn(name = "alert_id"))
    private Set<String> emails;

    @Column(name = "phone")
    @ElementCollection
    @CollectionTable(name = "iotw_alert_phone", joinColumns = @JoinColumn(name = "alert_id"))
    private Set<String> phoneNumbers;

}
