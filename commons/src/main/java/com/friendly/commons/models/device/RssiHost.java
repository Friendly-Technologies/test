package com.friendly.commons.models.device;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RssiHost {
    private String name;
    private Integer value;
    private Instant createdIso;
    private String created;
}

