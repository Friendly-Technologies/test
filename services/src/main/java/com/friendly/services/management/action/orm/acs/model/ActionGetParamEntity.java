package com.friendly.services.management.action.orm.acs.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ug_get_param")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionGetParamEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
    @Column(name = "name_id")
    private Long nameId;
    @Column(name = "names")
    private Boolean names;
    @Column(name = "values_attr")
    private Boolean valuesAttributes;
    @Column(name = "attributes")
    private Boolean attributes;
}
