package com.friendly.services.management.action.orm.acs.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ug_op_uninstall")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionOpUninstallEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name_id")
    private Integer nameId;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "version")
    private String version;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
}
