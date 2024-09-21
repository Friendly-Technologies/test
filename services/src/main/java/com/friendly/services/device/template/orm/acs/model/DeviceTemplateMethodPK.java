package com.friendly.services.device.template.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTemplateMethodPK implements Serializable {
    @Column(name = "product_group_id")
    private Long groupId;

    @Column(name = "method_name_id")
    private Long methodNameId;
}
