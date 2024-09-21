package com.friendly.commons.models.device.frame.response;

import com.friendly.commons.models.device.frame.SpeedTest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSpeedTestResponse {
    private List<SpeedTest> upload;
    private List<SpeedTest> download;
}
