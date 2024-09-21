package com.friendly.services.device.method.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_method")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpeMethodEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long cpeId;

    @Column(name = "method_name_id")
    private Long methodNameId;

}