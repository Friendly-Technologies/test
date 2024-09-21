package com.friendly.services.device.info.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

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
@Table(name = "cpe")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CpeEntity extends AbstractEntity<Long> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "location_id")
    private Integer domainId;

    @Formula("(select case when d.name is null then 'Super domain' else d.name end from cpe c " +
            "left join isp d on c.location_id = d.id where c.id = id)")
    private String domainName;

    @Column(name = "product_class_id")
    private Integer productClassId;

    @Column(name = "is_online")
    private Integer isOnline;

    @Column(name = "serial")
    private String serial;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "firmware")
    private String firmware;

    @Column(name = "protocol_id")
    private Integer protocolId;

}
