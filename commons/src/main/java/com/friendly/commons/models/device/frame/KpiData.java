package com.friendly.commons.models.device.frame;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiData {
    private String value;
    private String created;
    private Instant createdIso;
    private Long kpiId;
}
