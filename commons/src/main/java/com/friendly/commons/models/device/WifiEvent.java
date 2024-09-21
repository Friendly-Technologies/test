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
public class WifiEvent {
    private String name;
    private String frequency;
    private Integer channel;
    private Integer value;
    private String created;
    private Instant createdIso;
}
