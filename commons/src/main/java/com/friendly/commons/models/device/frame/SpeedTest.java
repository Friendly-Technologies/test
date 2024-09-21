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
public class SpeedTest {
  private String value;
  private Instant createdIso;
  private String created;
}
