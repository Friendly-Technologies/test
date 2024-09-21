package com.friendly.services.management.profiles.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "profile_file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileFileEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "delay_seconds")
    private Integer delaySeconds;

    @Column(name = "failure_url")
    private String failureUrl;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "file_type_id")
    private Integer fileTypeId;

    @Column(name = "password")
    private String password;

    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "success_url")
    private String successUrl;

    @Column(name = "target_file_name")
    private String targetFileName;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updator;

    @Column(name = "url")
    private String url;

    @Column(name = "username")
    private String username;

    @Column(name = "reset_session")
    private Boolean resetSession;

    @Column(name = "send_bytes")
    private Boolean sendBytes;

    @Column(name = "version")
    private String version;

    @Column(name = "delivery_protocol")
    private Byte deliveryProtocol;

    @Column(name = "delivery_method")
    private Byte deliveryMethod;

    @Column(name = "newest")
    private Boolean newest;
}
