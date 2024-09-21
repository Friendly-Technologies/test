package com.friendly.commons.models.device.response;

import com.friendly.commons.models.device.RssiHost;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RssiHostsResponse {
    private List<RssiHost> items;
}
