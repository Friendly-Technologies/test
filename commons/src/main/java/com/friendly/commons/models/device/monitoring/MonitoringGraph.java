package com.friendly.commons.models.device.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Model that represents API version of Device Diagnostic Detail
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringGraph implements Serializable {

    private Long nameId;
    private String shortName;
    private String fullName;
    private String type;

    private List<ParameterMonitoring> parameters;

}
