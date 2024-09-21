package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DiagIpPing {
    private Integer value;
    private String serial;
    private Instant createdIso;
    private String created;

}
