package com.friendly.commons.models.device.frame.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QoeFrameDetails {
    private Integer value;
    private String date;
}
