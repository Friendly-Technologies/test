package com.friendly.services.management.groupupdate.orm.acs.model;

import com.friendly.services.management.groupupdate.dto.enums.SourceType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Model that represents persistence version of Manufacturer
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "update_group_child")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupChildEntity extends AbstractEntity<Integer> {
    Integer customViewId;
    SourceType cpeSourceType;

    Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UpdateGroupEntity parent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductClassGroupEntity productClass;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupUpdateChild")
    private List<UpdateGroupDeviceEntity> devices;
}
