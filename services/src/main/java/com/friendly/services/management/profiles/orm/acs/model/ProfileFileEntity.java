package com.friendly.services.management.profiles.orm.acs.model;

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
 * Model that represents persistence version of Manufacturer
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "profile_file")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileFileEntity extends AbstractEntity<Long> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "file_type_id")
    private Integer fileTypeId;

    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "url")
    private String url;

}
