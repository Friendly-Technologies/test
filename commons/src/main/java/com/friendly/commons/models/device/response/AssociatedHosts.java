package com.friendly.commons.models.device.response;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssociatedHosts {
    private List<WifiLan> wifi;
    private List<WifiLan> lan;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class WifiLan {
        private Instant createdIso;
        private String created;
        private Integer value;
    }

}
