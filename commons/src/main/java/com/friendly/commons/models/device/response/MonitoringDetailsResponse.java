package com.friendly.commons.models.device.response;

import com.friendly.commons.models.device.monitoring.MonitoringDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class MonitoringDetailsResponse {
    Set<MonitoringDetail> items;
}
