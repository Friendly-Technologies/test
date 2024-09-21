package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.commons.models.settings.security.UnderlyingProtocolType;
import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iot_sec_conf_details")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspMtpEntity extends AbstractEntity<Integer> {
    @JoinColumn(name = "security_configuration_id")
    @ManyToOne
    private SecurityUspEntity security;

    @Enumerated(EnumType.STRING)
    @Column(name = "underlying_protocol_type")
    private UnderlyingProtocolType protocolType;

    @Enumerated(EnumType.STRING)
    @Column(name = "security_mode")
    private SecurityModeType securityType;


    @Column(name = "psk_identity")
    private String pskIdentity;

    @Column(name = "psk_secret_key")
    private String pskSecretKey;

    @Column(name = "client_rpk")
    private String clientRPK;

    @Column(name = "server_rpk")
    private String serverRPK;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "trusted_cert_chain")
    private String certificate;

    @Column(name = "trusted_cert_chain_alias")
    private String customAlias;

    @Column(name = "server_cert_chain")
    private String privateRPK;

}
