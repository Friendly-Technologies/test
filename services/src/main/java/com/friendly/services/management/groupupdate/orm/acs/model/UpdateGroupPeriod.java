package com.friendly.services.management.groupupdate.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;


@Entity
@Table(name = "ug_period")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupPeriod extends AbstractEntity<Integer> {

    Integer hourFrom;
    Integer minuteFrom;
    Integer hourTo;
    Integer minuteTo;
    Integer amount;
    @Column(name = "inter")
    Integer interval;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UpdateGroupEntity groupUpdate;
    String creator;
    Instant created;
    Instant updated;
    Instant updator;

}
