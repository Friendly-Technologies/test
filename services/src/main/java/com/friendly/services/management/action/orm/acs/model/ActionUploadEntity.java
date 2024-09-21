package com.friendly.services.management.action.orm.acs.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ug_upload")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ActionUploadEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
    @Column(name = "delay_seconds")
    private Integer delaySeconds;
    @Column(name = "filename")
    private String filename;
    @Column(name = "file_type_id")
    private Integer fileTypeId;
    @Column(name = "password")
    private String password;
    @Column(name = "url")
    private String url;
    @Column(name = "username")
    private String username;
    @Column(name = "instance")
    private Integer instance;
}
