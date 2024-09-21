package com.friendly.commons.models.device.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model that represents API version of Device Monitoring Detail
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringDetail implements Serializable {

    private Long nameId;
    private String shortName;
    private String fullName;
    private String value;
    private Boolean isDefault;
    private Boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoringDetail that = (MonitoringDetail) o;
        return nameId.equals(that.nameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameId);
    }
}
