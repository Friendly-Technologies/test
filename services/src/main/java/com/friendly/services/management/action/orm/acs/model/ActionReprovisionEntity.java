package com.friendly.services.management.action.orm.acs.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ug_reprovision")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionReprovisionEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
    @Column(name = "prov_attrib")
    private Boolean provAttrib;
    @Column(name = "custom_rpc")
    private Boolean customRpc;
    @Column(name = "prov_object")
    private Boolean provObject;
    @Column(name = "profile")
    private Boolean profile;
    @Column(name = "provision")
    private Boolean provision;
    @Column(name = "file")
    private Boolean file;

}
