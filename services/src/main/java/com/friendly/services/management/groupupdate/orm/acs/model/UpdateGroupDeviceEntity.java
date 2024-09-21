package com.friendly.services.management.groupupdate.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Model that represents persistence version of Manufacturer
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ug_cpe")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupDeviceEntity extends AbstractEntity<Integer> {
    @Column(name = "ug_id")
    private Long groupUpdateChildId;

    @Column(name = "cpe_id")
    private Long cpeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ug_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UpdateGroupChildEntity groupUpdateChild;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpe_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DeviceEntity device;
}
