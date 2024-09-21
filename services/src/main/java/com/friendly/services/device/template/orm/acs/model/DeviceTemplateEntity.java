package com.friendly.services.device.template.orm.acs.model;

import com.friendly.services.device.parameterstree.orm.acs.model.AbstractParameterEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "device_template")
@Data
@SuperBuilder
@NoArgsConstructor
@IdClass(DeviceTemplatePK.class)
public class DeviceTemplateEntity extends AbstractParameterEntity {

    @Id
    @Column(name = "product_group_id")
    private Long groupId;

    @Id
    @Column(name = "name_id")
    private Long nameId;

    @Column(name = "value")
    private String value;

    @Column(name = "writeable")
    private Boolean writeable;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "name_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CpeParameterNameEntity parameterName;

    @Override
    @Transient
    public Long getCpeId() {
        return groupId;
    }
}
