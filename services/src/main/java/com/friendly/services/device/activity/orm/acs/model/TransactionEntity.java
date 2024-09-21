package com.friendly.services.device.activity.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
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
 * Model that represents persistence version of Transaction
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "transaction")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity extends AbstractEntity<Integer> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "name")
    private String name;

}
