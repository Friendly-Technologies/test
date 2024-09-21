package com.friendly.services.settings.acs.orm.acs.model;

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
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_white_list_serials")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WhiteListSerialEntity extends AbstractEntity<Integer> {

    @Column(name = "white_list_id")
    private Integer whiteListId;

    @Column(name = "serial")
    private String serial;


}
