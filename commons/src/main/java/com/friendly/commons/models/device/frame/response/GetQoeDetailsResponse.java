package com.friendly.commons.models.device.frame.response;

import com.friendly.commons.models.device.frame.KpiData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetQoeDetailsResponse {
    List<KpiData> items;
}
