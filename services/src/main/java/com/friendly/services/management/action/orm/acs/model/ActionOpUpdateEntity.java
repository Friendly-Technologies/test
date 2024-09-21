package com.friendly.services.management.action.orm.acs.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ug_op_update")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionOpUpdateEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "url")
    private String url;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "version")
    private String version;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
}
