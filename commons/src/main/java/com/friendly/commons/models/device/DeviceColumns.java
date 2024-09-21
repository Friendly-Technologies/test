package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceColumns implements Serializable {

    private String columnKey;
    private String columnName;
    private boolean canVisible;
    private boolean canSort;
    private boolean canFilter;

    public DeviceColumns(final String columnKey,
                         final boolean canSort,
                         final boolean canFilter,
                         final boolean canVisible) {
        this.columnKey = columnKey;
        this.canSort = canSort;
        this.canFilter = canFilter;
        this.canVisible = canVisible;
        this.columnName = columnKey;
    }


}
