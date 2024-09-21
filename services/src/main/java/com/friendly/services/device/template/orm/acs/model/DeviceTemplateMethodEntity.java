package com.friendly.services.device.template.orm.acs.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "device_template_method")
@Data
@SuperBuilder
@NoArgsConstructor
@IdClass(DeviceTemplateMethodPK.class)
public class DeviceTemplateMethodEntity implements Serializable {
    @Id
    @Column(name = "product_group_id")
    private Long groupId;

    @Id
    @Column(name = "method_name_id")
    private Long methodNameId;
}
