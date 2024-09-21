package com.friendly.commons.models.settings;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlertsResponse implements Serializable {
    private Alerts defaultAlerts;
    private Alerts domainSpecificAlerts;
}
