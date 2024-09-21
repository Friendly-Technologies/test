package com.friendly.commons.models.device.setting;

import com.friendly.commons.models.tree.TreeObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceObject extends TreeObject<DeviceObject, DeviceParameter> {

    private Long id;
    private Long nameId;
    private Boolean canAddObject;
    private Boolean canDeleteObject;
}
