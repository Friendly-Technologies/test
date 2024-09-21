package com.friendly.services.management.action.orm.acs.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ug_set_param_value")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionSetValueEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
    @Column(name = "name_id")
    private Long nameId;
    @Column(name = "value")
    private String value;
}
