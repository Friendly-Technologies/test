package com.friendly.services.management.action.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ug_download")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionDownloadEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
    @Column(name = "delay_Seconds")
    private Integer delaySeconds;
    @Column(name = "failure_url")
    private String failureUrl;
    @Column(name = "filename")
    private String filename;
    @Column(name = "file_size")
    private Integer fileSize;
    @Column(name = "file_type_id")
    private Integer fileTypeId;
    @Column(name = "password")
    private String password;
    @Column(name = "success_url")
    private String successUrl;
    @Column(name = "target_file_name")
    private String targetFileName;
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
    private Integer deliveryProtocol;
    @Column(name = "delivery_method")
    private Integer deliveryMethod;
    @Column(name = "newest")
    private Boolean newest;
}
