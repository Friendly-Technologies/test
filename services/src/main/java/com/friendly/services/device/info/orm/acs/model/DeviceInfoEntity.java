package com.friendly.services.device.info.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoEntity extends AbstractEntity<Long> {

    @Column(name = "serial")
    private String serial;

    @Column(name = "firmware")
    private String firmware;

    @Column(name = "is_online")
    private Integer isOnline;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "protocol_id")
    private Integer protocolId;

    @Column(name = "active_conn_name_id")
    private Integer activeConnectionNameId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_class_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductClassEntity productClass;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial", referencedColumnName = "serial", insertable = false, updatable = false)
    private List<CustomDeviceEntity> customDevice;

}
