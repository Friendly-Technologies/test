package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.commons.models.settings.security.MaskType;
import com.friendly.commons.models.settings.security.ServerType;
import com.friendly.commons.models.settings.security.oscore.AeadAlgorithmType;
import com.friendly.commons.models.settings.security.oscore.HmacAlgorithmType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.Instant;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lwm2m_security")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SecurityLwm2mEntity extends AbstractEntity<Integer> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updater;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "endpoint_mask")
    private String mask;

    @Column(name = "endpoint_mask_type")
    @Enumerated(EnumType.STRING)
    private MaskType maskType;

    @Column(name = "server_type")
    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    @Column(name = "security_mode")
    private Integer securityMode;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "ec_parameters")
    private String parameters;

    @Column(name = "comment_data")
    private String commentData;

    @Column(name = "comment")
    private String comment;

    @Column(name = "os_aead_alg_id")
    private AeadAlgorithmType aeadAlgorithmType;

    @Column(name = "os_hmac_alg_id")
    private HmacAlgorithmType hmacAlgorithmType;

    @Column(name = "os_master_secret")
    private String masterSecret;

    @Column(name = "os_master_salt")
    private String masterSalt;

    @Column(name = "os_recipient_id")
    private String recipientId;

    @Column(name = "os_sender_id")
    private String senderId;

    @Column(name = "identity")
    private String identity;

}
