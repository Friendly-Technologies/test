package com.friendly.services.management.action.orm.acs.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ug_op_install")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionOpInstallEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name_id")
    private Integer nameId;
    @Column(name = "url")
    private String url;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
    @Column(name = "uuid")
    private String uuid;
}
