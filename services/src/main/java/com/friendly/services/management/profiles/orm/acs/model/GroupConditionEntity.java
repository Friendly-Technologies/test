package com.friendly.services.management.profiles.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "group_condition")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupConditionEntity extends AbstractEntity<Long> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "domain_id")
    private Integer domainId;

    @Formula("(select case when d.name is null then 'Super domain' else d.name end from cpe c " +
            "left join isp d on c.location_id = d.id where c.id = id)")
    private String domainName;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "name")
    private String name;

    @Column(name = "updated")
    private Instant updated;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductClassGroupEntity productClassGroup;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_condition_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<GroupConditionFilterEntity> filters;
}
