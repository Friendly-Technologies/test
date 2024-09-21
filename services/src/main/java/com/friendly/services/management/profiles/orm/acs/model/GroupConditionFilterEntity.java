package com.friendly.services.management.profiles.orm.acs.model;

import com.friendly.commons.models.view.ConditionType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.management.profiles.ConditionGroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "group_condition_filter")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupConditionFilterEntity extends AbstractEntity<Long> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "group_condition_id")
    private Long groupConditionId;

    @Column(name = "group_type")
    private ConditionGroupType groupType;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @Column(name = "type")
    private ConditionType type;
}
