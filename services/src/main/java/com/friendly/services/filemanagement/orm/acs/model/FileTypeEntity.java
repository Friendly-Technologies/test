package com.friendly.services.filemanagement.orm.acs.model;

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
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "file_type")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FileTypeEntity extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;

    @Column(name = "protocol_id")
    private Integer protocolId;

    @Column(name = "type")
    private String type;

}
