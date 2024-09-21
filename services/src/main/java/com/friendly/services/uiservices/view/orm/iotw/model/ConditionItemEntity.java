package com.friendly.services.uiservices.view.orm.iotw.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_view_condition_item")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionItemEntity extends AbstractEntity<Long> {
    @Column(name = "view_id")
    private Long viewId;

    @Column(name = "view_index")
    private Integer viewIndex;

    @Formula("(SELECT v.name FROM iotw_view v WHERE v.id = view_id)")
    private String viewName;

    @ManyToOne
    @JoinColumn(name = "view_id", insertable = false, updatable = false)
    private ViewEntity view;
}
