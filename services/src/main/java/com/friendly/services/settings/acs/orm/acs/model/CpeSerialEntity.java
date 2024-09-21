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
import java.time.Instant;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_serial")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CpeSerialEntity extends AbstractEntity<Long> {

    @Column(name = "cpe_id")
    private Long deviceId;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "serial")
    private String serial;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "protocol_id")
    private Integer protocolId;

}
