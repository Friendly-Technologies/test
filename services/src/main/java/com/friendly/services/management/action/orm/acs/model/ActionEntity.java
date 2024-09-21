package com.friendly.services.management.action.orm.acs.model;

import com.friendly.services.device.method.orm.acs.model.CpeMethodNameEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "ug_task")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionEntity extends AbstractActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ug_id")
    private Integer ugId;

    @Column(name = "task_type")
    private Integer taskType;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "owner_type")
    private Integer ownerType;

    @Column(name = "method_name_id")
    private Long methodNameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_name_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CpeMethodNameEntity methodNameEntity;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionDownloadEntity> actionDownloadList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionCallApiEntity> actionCallApiList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionDiagnosticEntity> actionDiagnosticList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionSetValueEntity> actionSetValueList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionSetAttributesEntity> actionSetAttributesList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionReprovisionEntity> actionReprovisionList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionCustomRpcEntity> actionCustomRpcList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionUploadEntity> actionUploadList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionGetParamEntity> actionGetParamList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionOpInstallEntity> actionOpInstallList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionOpUninstallEntity> actionOpUninstallList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ActionOpUpdateEntity> actionOpUpdateList;
}
