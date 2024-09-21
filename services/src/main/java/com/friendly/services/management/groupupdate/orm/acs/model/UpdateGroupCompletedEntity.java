package com.friendly.services.management.groupupdate.orm.acs.model;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateDeviceStateType;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Model that represents persistence version of Manufacturer
 *
 * @author Friendly Tech
 * @since 0.0.2
 */

@Entity
@Table(name = "ug_cpe_completed")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupCompletedEntity extends AbstractEntity<Integer> {

    @Column(name = "ug_id")
    private Integer updateGroupId;

    Integer ugChildId;

    @Column(name = "cpe_id")
    private Long deviceId;

    @Column(name = "state")
    private GroupUpdateDeviceStateType state;

    @Column(name = "updated")
    private Instant updated;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpe_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DeviceEntity device;
}
