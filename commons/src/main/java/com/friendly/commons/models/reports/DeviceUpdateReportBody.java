package com.friendly.commons.models.reports;

import com.friendly.commons.models.FieldSort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Model that represents API version of IOT controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUpdateReportBody implements Serializable {
    private Integer domainId;
    private Long userId;
    private DeviceActivityType activityType;
    private String serial;
    private Instant from;
    private Instant to;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;


}