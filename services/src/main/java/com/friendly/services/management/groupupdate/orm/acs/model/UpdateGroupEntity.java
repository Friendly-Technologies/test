package com.friendly.services.management.groupupdate.orm.acs.model;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

/**
 * Model that represents persistence version of Manufacturer
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "update_group")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupEntity extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;

    @Formula("(select case when d.name is null then 'Super domain' else d.name end from update_group c " +
            "left join isp d on c.location_id = d.id where c.id = id)")
    private String domain;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "creator")
    private String creator;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "scheduled")
    private Instant scheduled;

    Integer threshold;

    Boolean push;

    Boolean onlineCpeOnly;

    Boolean stopOnFail;

    @Column(name = "state")
    private GroupUpdateStateType state;
    @Column(name = "random_cnt")
    Integer random;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<UpdateGroupChildEntity> children;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupUpdate")
    private List<UpdateGroupPeriod> periods;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupUpdate")
    private List<UpdateGroupReactivate> reactivates;
}
