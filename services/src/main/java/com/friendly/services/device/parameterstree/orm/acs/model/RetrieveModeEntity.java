package com.friendly.services.device.parameterstree.orm.acs.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_retrieve_method")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveModeEntity extends AbstractEntity<Integer> {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductClassGroupEntity productGroup;

}
