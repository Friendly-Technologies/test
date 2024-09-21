package com.friendly.services.management.groupupdate.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Model that represents persistence version of Manufacturer
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ug_transaction")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupTransactionEntity extends AbstractEntity<Integer> {

    @Column(name = "ug_id")
    private Integer updateGroupId;

    @Column(name = "transaction_id")
    private Long transactionId;

}
