package com.friendly.services.settings.snmpserver.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.SnmpVersionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model that represents persistence version of SNMP Server Settings
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode
@Entity
@Table(name = "iotw_snmp_server")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnmpServerEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private ClientType id;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private String port;

    @Column(name = "community")
    private String community;

    @Column(name = "version")
    @Enumerated(EnumType.STRING)
    private SnmpVersionType version;


}
