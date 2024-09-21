package com.friendly.services.management.action.orm.acs.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ug_call_api")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionCallApiEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ug_task_id")
    private Integer ugTaskId;
    @Column(name = "api_url")
    private String apiUrl;
    @Column(name = "api_request")
    private String apiRequest;
    @Column(name = "api_method_name")
    private String apiMethodName;
}
