package com.friendly.commons.models.device.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

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
public class ParameterMonitoring implements Serializable {

    private String value;
    private Instant timeIso;
    private String time;

}
